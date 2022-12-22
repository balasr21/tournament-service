package com.img.sports.tournament.api.rest.resources;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.img.sports.tournament.api.CustomerApiDelegate;
import com.img.sports.tournament.model.CreateCustomerRequest;
import com.img.sports.tournament.model.CreateCustomerResponse;
import com.img.sports.tournament.model.CustomerMatchesResponse;
import com.img.sports.tournament.model.SummaryEnum;
import com.img.sports.tournament.model.UpdateLicenseRequest;
import com.img.sports.tournament.model.UpdateLicenseResponse;
import com.img.sports.tournament.service.CustomerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class CustomerController implements CustomerApiDelegate {

    private final CustomerService customerService;

    @Override
    public Mono<ResponseEntity<CreateCustomerResponse>> createCustomer(Mono<CreateCustomerRequest> createCustomerRequest, ServerWebExchange exchange) {
        return customerService.createCustomer(createCustomerRequest).
                              flatMap(createCustomerResponse ->
                                              Mono.just(ResponseEntity.status(HttpStatus.CREATED).
                                                                      body(createCustomerResponse)));
    }

    @Override
    public Mono<ResponseEntity<CustomerMatchesResponse>> getCustomer(UUID customerId, String summary, ServerWebExchange exchange) {
        return customerService.getCustomerLicensedMatches(customerId, summary != null ? SummaryEnum.valueOf(summary) : null)
                              .flatMap(getMatchesResponse ->
                                               Mono.just(ResponseEntity.status(HttpStatus.OK)
                                                                       .body(getMatchesResponse))
                              );
    }

    @Override
    public Mono<ResponseEntity<UpdateLicenseResponse>> updateCustomerLicense(UUID customerId, Mono<UpdateLicenseRequest> updateLicenseRequest, ServerWebExchange exchange) {
        return customerService.updateCustomerLicense(customerId, updateLicenseRequest).
                              flatMap(updateLicenseResponse -> Mono.just(ResponseEntity.status(HttpStatus.ACCEPTED).body(updateLicenseResponse)));
    }
}
