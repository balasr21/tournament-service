package com.img.sports.tournament.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.img.sports.tournament.converter.CustomerConverter;
import com.img.sports.tournament.model.CreateCustomerRequest;
import com.img.sports.tournament.model.CreateCustomerResponse;
import com.img.sports.tournament.model.Customer;
import com.img.sports.tournament.model.CustomerLicensedMatches;
import com.img.sports.tournament.model.CustomerMatchesResponse;
import com.img.sports.tournament.model.Match;
import com.img.sports.tournament.model.MatchDetails;
import com.img.sports.tournament.model.Meta;
import com.img.sports.tournament.model.UpdateLicenseRequest;
import com.img.sports.tournament.model.UpdateLicenseResponse;
import com.img.sports.tournament.repository.CustomerLicensedMatchesRepository;
import com.img.sports.tournament.repository.CustomerRepository;
import com.img.sports.tournament.repository.MatchRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class CustomerServiceTest {


    @Mock
    CustomerRepository customerRepository;
    @Mock
    MatchRepository matchRepository;
    @Mock
    CustomerLicensedMatchesRepository customerLicensedMatchesRepository;
    @Mock
    CustomerConverter customerConverter;
    @InjectMocks
    CustomerService customerService;

    @Test
    void createCustomer() {
        var customer = Customer.builder().id(UUID.randomUUID()).firstName("John").lastName("Doe").dateOfBirth(LocalDate.now()).build();
        var response = CreateCustomerResponse.builder().customerId(UUID.randomUUID()).firstName("John").lastName("Doe").dateOfBirth(LocalDate.now()).build();
        when(customerConverter.mapCustomerToEntity(any())).thenReturn(customer);
        when(customerRepository.save(any())).thenReturn(Mono.just(customer));
        when(customerConverter.mapToCustomerResponse(any())).thenReturn(response);
        StepVerifier.create(customerService.createCustomer(Mono.justOrEmpty(CreateCustomerRequest
                                                                                    .builder()
                                                                                    .firstName("John")
                                                                                    .lastName("Doe")
                                                                                    .dateOfBirth(LocalDate.now()).build())))
                    .expectNextMatches(createCustomerResponse -> {
                        Assertions.assertNotNull(createCustomerResponse);
                        Assertions.assertNotNull(createCustomerResponse.getCustomerId());
                        Assertions.assertEquals("John", createCustomerResponse.getFirstName());
                        Assertions.assertEquals("Doe", createCustomerResponse.getLastName());
                        return true;
                    }).verifyComplete();
    }

    @Test
    void updateCustomerLicense_success() {
        var match = Match.builder().id(UUID.randomUUID()).durationInMinutes(2l).playerA("playerA").playerB("playerB").startDateTime(
                OffsetDateTime.now()).build();
        var updateCustomerLicenseResponse = UpdateLicenseResponse.builder().licenseId(UUID.randomUUID().toString()).data(List.of(match.getId())).build();
        when(matchRepository.findAllById(anyList())).thenReturn(Flux.just(match));
        when(customerLicensedMatchesRepository.saveAll(anyList())).thenReturn(Flux.empty());
        when(customerConverter.mapToCustomerLicenseResponse(any())).thenReturn(updateCustomerLicenseResponse);
        StepVerifier.create(customerService.updateCustomerLicense(UUID.randomUUID(), Mono.just(UpdateLicenseRequest.builder().data(List.of(match.getId())).build())))
                    .expectNextMatches(updateLicenseResponse -> {
                        Assertions.assertNotNull(updateLicenseResponse);
                        return true;
                    }).verifyComplete();

    }

    @Test
    void testUpdateCustomerLicense_If_requested_Matches_not_found() {
        when(matchRepository.findAllById(anyList())).thenReturn(Flux.empty());
        StepVerifier.create(customerService.updateCustomerLicense(UUID.randomUUID(), Mono.just(UpdateLicenseRequest.builder().data(List.of(UUID.randomUUID())).build())))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("No matches found for the received request"))
                    .verify();
    }

    @Test
    void testUpdateCustomerLicense_exception_during_saving_customer_license_match_relationship() {
        when(matchRepository.findAllById(anyList())).thenReturn(Flux.empty());
        when(customerLicensedMatchesRepository.saveAll(anyList())).thenReturn(Flux.error(new RuntimeException("Error in saving customer licensed matches")));
        StepVerifier.create(customerService.updateCustomerLicense(UUID.randomUUID(), Mono.just(UpdateLicenseRequest.builder().data(List.of(UUID.randomUUID())).build())))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && ((RuntimeException) throwable).getMessage().equals("Error in saving customer licensed matches"));
    }

    @Test
    void testGetCustomerLicensedMatches() {
        var customerId = UUID.randomUUID();
        var matchId = UUID.randomUUID();
        var licenseId = UUID.randomUUID();
        var startDateTime = OffsetDateTime.now();
        var match = Match.builder().id(matchId).durationInMinutes(2l).playerA("playerA").playerB("playerB").startDateTime(
                startDateTime).build();
        when(customerLicensedMatchesRepository.findAllCustomerLicensedMatchesByCustomerId(any())).thenReturn(
                Flux.just(CustomerLicensedMatches.builder().matchId(matchId).licenseId(licenseId).customerId(customerId).build()));
        when(matchRepository.findAllById(anyList())).thenReturn(Flux.just(match));
        when(customerConverter.mapCustomerToMatchDetails(anyList(), any())).thenReturn(CustomerMatchesResponse.builder().meta(Meta.builder().size(1l).next(null).page(0l).build()).data(List.of(
                MatchDetails.builder().matchId(matchId).summary("playerA vs playerB").startDateTime(startDateTime).durationInMinutes(2l).playerA("playerA").playerB("playerB").build())).build());
        StepVerifier.create(customerService.getCustomerLicensedMatches(customerId, null)).expectNextMatches(customerMatchesResponse -> {
            Assertions.assertNotNull(customerMatchesResponse);
            Assertions.assertNotNull(customerMatchesResponse.getMeta());
            Assertions.assertNotNull(customerMatchesResponse.getData());
            org.assertj.core.api.Assertions.assertThat(customerMatchesResponse.getData()).hasSize(1);
            Assertions.assertAll(() -> Assertions.assertEquals(matchId, customerMatchesResponse.getData().get(0).getMatchId()),
                                 () -> Assertions.assertEquals("playerA", customerMatchesResponse.getData().get(0).getPlayerA()),
                                 () -> Assertions.assertEquals("playerB", customerMatchesResponse.getData().get(0).getPlayerB()),
                                 () -> Assertions.assertEquals("playerA vs playerB", customerMatchesResponse.getData().get(0).getSummary()),
                                 () -> Assertions.assertEquals(2l, customerMatchesResponse.getData().get(0).getDurationInMinutes()),
                                 () -> Assertions.assertEquals(startDateTime, customerMatchesResponse.getData().get(0).getStartDateTime())
            );
            return true;
        }).verifyComplete();
    }

    @Test
    void testGetCustomerLicensedMatches_if_no_matches_found_for_customer() {
        var customerId = UUID.randomUUID();
        when(customerLicensedMatchesRepository.findAllCustomerLicensedMatchesByCustomerId(any())).thenReturn(Flux.empty());
        when(customerConverter.mapCustomerToMatchDetails(anyList(), any())).thenReturn(CustomerMatchesResponse
                                                                                               .builder()
                                                                                               .meta(Meta
                                                                                                             .builder()
                                                                                                             .page(0l) // Pagination should be handled in future
                                                                                                             .size(0l) // Pagination should be handled in future
                                                                                                             .next(null) // Pagination should be handled in future
                                                                                                             .build())
                                                                                               .data(new ArrayList<>()).build());
        StepVerifier.create(customerService.getCustomerLicensedMatches(customerId, null)).expectNextMatches(customerMatchesResponse -> {
            Assertions.assertNotNull(customerMatchesResponse);
            Assertions.assertNotNull(customerMatchesResponse.getMeta());
            Assertions.assertNotNull(customerMatchesResponse.getData());
            org.assertj.core.api.Assertions.assertThat(customerMatchesResponse.getData()).hasSize(0);
            return true;
        }).verifyComplete();
    }

    @Test
    void testGetCustomerLicensedMatches_if_customer_licensed_matches_relationship_exists_but_no_match_found() {
        var customerId = UUID.randomUUID();
        var matchId = UUID.randomUUID();
        var licenseId = UUID.randomUUID();
        when(customerLicensedMatchesRepository.findAllCustomerLicensedMatchesByCustomerId(any())).thenReturn(
                Flux.just(CustomerLicensedMatches.builder().matchId(matchId).licenseId(licenseId).customerId(customerId).build()));
        when(matchRepository.findAllById(anyList())).thenReturn(Flux.empty());
        StepVerifier.create(customerService.getCustomerLicensedMatches(customerId, null))
                    .expectErrorMatches(
                            throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Data error: Customer license relationship exists but matches are not available"))
                    .verify();
    }

}