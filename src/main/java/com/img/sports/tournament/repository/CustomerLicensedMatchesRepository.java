package com.img.sports.tournament.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.img.sports.tournament.model.CustomerLicensedMatches;

import reactor.core.publisher.Flux;

public interface CustomerLicensedMatchesRepository extends ReactiveCrudRepository<CustomerLicensedMatches, UUID> {

    @Query("select * from customer_licensed_matches where customer_id=:customerId::varchar")
    Flux<CustomerLicensedMatches> findAllCustomerLicensedMatchesByCustomerId(UUID customerId);

}
