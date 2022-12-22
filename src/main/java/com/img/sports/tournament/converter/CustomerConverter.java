package com.img.sports.tournament.converter;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.img.sports.tournament.constants.SummaryConstants;
import com.img.sports.tournament.model.CreateCustomerRequest;
import com.img.sports.tournament.model.CreateCustomerResponse;
import com.img.sports.tournament.model.Customer;
import com.img.sports.tournament.model.CustomerMatchesResponse;
import com.img.sports.tournament.model.Match;
import com.img.sports.tournament.model.MatchDetails;
import com.img.sports.tournament.model.Meta;
import com.img.sports.tournament.model.SummaryEnum;
import com.img.sports.tournament.model.UpdateLicenseResponse;

import reactor.util.function.Tuple2;

@Component
public class CustomerConverter {

    /**
     * maps customer request to customer entity
     * @param customerRequest
     * @return
     */
    public Customer mapCustomerToEntity(CreateCustomerRequest customerRequest) {
        return Customer
                .builder()
                .firstName(customerRequest.getFirstName())
                .lastName(customerRequest.getLastName())
                .dateOfBirth(customerRequest.getDateOfBirth())
                .status(Customer.Status.ACTIVE)
                .build();
    }


    /**
     * maps saved customer entity to customer response
     * @param customer
     * @return
     */
    public CreateCustomerResponse mapToCustomerResponse(Customer customer) {
        return CreateCustomerResponse
                .builder()
                .customerId(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .dateOfBirth(customer.getDateOfBirth())
                .build();
    }

    /**
     * maps license update response for customer
     * @param customerResponse
     * @return
     */
    public UpdateLicenseResponse mapToCustomerLicenseResponse(Tuple2<UUID, List<Match>> customerResponse) {
        var matchIds = customerResponse.getT2().stream().map(Match::getId).collect(Collectors.toList());
        return UpdateLicenseResponse.builder().licenseId(customerResponse.getT1().toString()).data(matchIds).build();
    }

    /**
     * Maps customer licensed matches to response object
     * @param matches
     * @param summary
     * @return
     */
    public CustomerMatchesResponse mapCustomerToMatchDetails(List<Match> matches, SummaryEnum summary) {

        if (CollectionUtils.isEmpty(matches)) {
            return CustomerMatchesResponse
                    .builder()
                    .meta(Meta
                                  .builder()
                                  .page(0l) // Pagination should be handled in future
                                  .size(0l) // Pagination should be handled in future
                                  .next(null) // Pagination should be handled in future
                                  .build())
                    .data(new ArrayList<>()).build();
        }

        return CustomerMatchesResponse
                .builder()
                .meta(Meta
                              .builder()
                              .page(0l) // Pagination should be handled in future
                              .size(matches != null ? matches.size() : 0l) // Pagination should be handled in future
                              .next(null) // Pagination should be handled in future
                              .build())
                .data(matches
                              .stream()
                              .map(match -> MatchDetails
                                      .builder()
                                      .matchId(match.getId())
                                      .playerA(match.getPlayerA())
                                      .playerB(match.getPlayerB())
                                      .durationInMinutes(match.getDurationInMinutes())
                                      .startDateTime(DateHelper.mapUTCToLocalTime(match.getStartDateTime()))
                                      .summary(generateSummary(match, summary))
                                      .build())
                              .collect(Collectors.toList()))
                .build();
    }

    private String generateSummary(Match match, SummaryEnum summary) {
        if (summary == null || summary.equals(SummaryEnum.AvB)) {
            return String.format(SummaryConstants.AvBSummary, match.getPlayerA(), match.getPlayerB());
        }
        return match.getStartDateTime().isAfter(OffsetDateTime.now()) ?
               String.format(SummaryConstants.AvBTimeSummaryFuture, match.getPlayerA(), match.getPlayerB(), ChronoUnit.MINUTES.between(OffsetDateTime.now(), match.getStartDateTime())) :
               String.format(SummaryConstants.AvBTimeSummaryPast, match.getPlayerA(), match.getPlayerB(), ChronoUnit.MINUTES.between(match.getStartDateTime(), OffsetDateTime.now()));
    }

}
