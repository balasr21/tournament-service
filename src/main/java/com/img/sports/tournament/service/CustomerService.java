package com.img.sports.tournament.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.img.sports.tournament.converter.CustomerConverter;
import com.img.sports.tournament.model.CreateCustomerRequest;
import com.img.sports.tournament.model.CreateCustomerResponse;
import com.img.sports.tournament.model.CustomerLicensedMatches;
import com.img.sports.tournament.model.CustomerMatchesResponse;
import com.img.sports.tournament.model.Match;
import com.img.sports.tournament.model.SummaryEnum;
import com.img.sports.tournament.model.UpdateLicenseRequest;
import com.img.sports.tournament.model.UpdateLicenseResponse;
import com.img.sports.tournament.repository.CustomerLicensedMatchesRepository;
import com.img.sports.tournament.repository.CustomerRepository;
import com.img.sports.tournament.repository.MatchRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Slf4j
@Service
@AllArgsConstructor
@EnableTransactionManagement
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final MatchRepository matchRepository;
    private final CustomerLicensedMatchesRepository customerLicensedMatchesRepository;
    private final CustomerConverter customerConverter;

    /**
     * Creates Customer based on request
     *
     * @param customerRequest
     * @return
     */
    @Transactional
    public Mono<CreateCustomerResponse> createCustomer(Mono<CreateCustomerRequest> customerRequest) {
        return customerRequest
                .map(customerConverter::mapCustomerToEntity)
                .flatMap(customerRepository::save)
                .map(customerConverter::mapToCustomerResponse)
                .doOnSuccess(s -> log.debug("Successfully created customer [{}]", s.getCustomerId()))
                .doOnError(throwable -> log.error("Error during creating customer [{}]", throwable));
    }

    /**
     * Updates customer licensed matches
     *
     * @param customerId
     * @param updateLicenseRequest
     * @return
     */
    @Transactional
    public Mono<UpdateLicenseResponse> updateCustomerLicense(UUID customerId, Mono<UpdateLicenseRequest> updateLicenseRequest) {
        return updateLicenseRequest
                .flatMap(request -> findAllMatchingMatches(request, customerId))
                .flatMap(matches -> generateAndSaveCustomerLicense(matches, customerId))
                .map(customerConverter::mapToCustomerLicenseResponse)
                .doOnSuccess(s -> log.debug("Successfully updated customer [{}]", customerId))
                .doOnError(throwable -> log.error("Error during updating customer licensed matches [{}]", throwable));
    }

    private Mono<List<Match>> findAllMatchingMatches(UpdateLicenseRequest request, UUID customerId) {
        log.debug("Checking existing match against request");
        return matchRepository.findAllById(request.getData())
                              .switchIfEmpty(Mono.defer(() -> {
                                  log.warn("No matches found for the received request for customer [{}]", customerId); // TODO Handle error
                                  return Mono.error(new RuntimeException("No matches found for the received request"));
                              }))
                              .collectList();
    }

    private Mono<Tuple2<UUID, List<Match>>> generateAndSaveCustomerLicense(List<Match> matches, UUID customerId) {
        var licenseId = UUID.randomUUID();
        log.debug("Generating new licenseId [{}] for customer [{}]", licenseId, customerId);
        var customerLicensedMatches =
                matches.stream()
                       .map(match -> CustomerLicensedMatches
                               .builder()
                               .licenseId(licenseId)
                               .customerId(customerId)
                               .matchId(match.getId())
                               .build())
                       .collect(Collectors.toList());
        return Mono.zip(Mono.just(licenseId), Mono.just(matches))
                   .delayUntil(response -> {
                       log.debug("Saving customer [{}] licensed matches", customerId);
                       return customerLicensedMatchesRepository.saveAll(customerLicensedMatches);
                   });
    }

    /**
     * Get customer licensed matches
     *
     * @param customerId
     * @return
     */
    public Mono<CustomerMatchesResponse> getCustomerLicensedMatches(UUID customerId, SummaryEnum summary) {
        return customerLicensedMatchesRepository.findAllCustomerLicensedMatchesByCustomerId(customerId)
                                                .switchIfEmpty(Mono.defer(() -> {
                                                    log.warn("Customer doesn't have any licensed matches");
                                                    return Mono.empty();
                                                }))
                                                .collectList()
                                                .flatMap(this::retrieveMatchesByMatchId)
                                                .map(matches -> customerConverter.mapCustomerToMatchDetails(matches, summary))
                                                .doOnSuccess(s -> log.debug("Successfully retrieved customer licensed matches [{}]", customerId))
                                                .doOnError(throwable -> log.error("Error during retrieving customer licensed matches [{}]", throwable));
    }

    private Mono<List<Match>> retrieveMatchesByMatchId(List<CustomerLicensedMatches> customerLicensedMatches) {

        if (CollectionUtils.isEmpty(customerLicensedMatches)) {
            return Mono.just(new ArrayList<>());
        }

        var matches = customerLicensedMatches
                .stream()
                .map(CustomerLicensedMatches::getMatchId)
                .collect(Collectors.toList());
        return matchRepository.findAllById(matches)
                              .switchIfEmpty(Mono.defer(() -> {
                                  log.warn("Customer license relationship exists but matches are not available");
                                  return Mono.error(new RuntimeException("Data error: Customer license relationship exists but matches are not available")); // TODO handle error
                              }))
                              .collectList();
    }

}
