package com.img.sports.tournament.api.rest.resources;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.img.sports.tournament.model.CreateCustomerRequest;
import com.img.sports.tournament.model.CreateCustomerResponse;
import com.img.sports.tournament.model.CreateTournamentRequest;
import com.img.sports.tournament.model.CreateTournamentResponse;
import com.img.sports.tournament.model.CustomerMatchesResponse;
import com.img.sports.tournament.model.MatchDetails;
import com.img.sports.tournament.model.UpdateLicenseRequest;
import com.img.sports.tournament.model.UpdateLicenseResponse;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class AllControllerTest {

    public static final String CREATE_TOURNAMENT_URI = "/tournament";

    public static final String GET_TOURNAMENT_URI = "/tournament/%s";
    public static final String CUSTOMERS_URI = "/customer";

    public static final String PATCH_CUSTOMERS_LICENSED_URI = "/customer/%s/matches";

    public static final String GET_CUSTOMER_MATCHES_URI = "/customer/%s/licensed/matches";

    @Autowired
    protected WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        webTestClient = webTestClient.mutate()
                                     .responseTimeout(Duration.ofMillis(3000000))
                                     .build();
    }

    @Test
    void testTournament_all_endpoints_success() throws JsonProcessingException {

        var matchStartDateTime = OffsetDateTime.now().plusDays(2);
        var playerA = "playerA";
        var playerB = "playerB";
        var matchDuration = 5l;
        var summary = "playerA vs playerB";

        // Create Tournament and match
        var createTournamentResponseAsString = webTestClient
                .post()
                .uri(CREATE_TOURNAMENT_URI)
                .body(BodyInserters.fromPublisher(Mono.just(CreateTournamentRequest
                                                                    .builder()
                                                                    .tournamentType(CreateTournamentRequest.TournamentTypeEnum.TENNIS)
                                                                    .durationInDays(matchDuration)
                                                                    .startDateTime(matchStartDateTime)
                                                                    .match(List.of(MatchDetails
                                                                                           .builder()
                                                                                           .startDateTime(matchStartDateTime)
                                                                                           .durationInMinutes(matchDuration)
                                                                                           .playerA(playerA)
                                                                                           .playerB(playerB)
                                                                                           .build()))
                                                                    .build()),
                                                  CreateTournamentRequest.class))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(String.class).returnResult().getResponseBody();

        var createTournamentResponse = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .readValue(createTournamentResponseAsString, CreateTournamentResponse.class);

        var matchIds = createTournamentResponse.getMatch().stream().map(MatchDetails::getMatchId).collect(Collectors.toList());
        var tournamentId = createTournamentResponse.getTournamentId();

        Assertions.assertAll(() -> Assertions.assertNotNull(createTournamentResponse),
                             () -> Assertions.assertNotNull(createTournamentResponse.getTournamentId()),
                             () -> Assertions.assertEquals(1, createTournamentResponse.getMatch().size()),
                             () -> Assertions.assertEquals("playerA", createTournamentResponse.getMatch().get(0).getPlayerA()),
                             () -> Assertions.assertEquals("playerB", createTournamentResponse.getMatch().get(0).getPlayerB()));

        // Get Tournament

        var getTournamentResponseAsString = webTestClient
                .get()
                .uri(String.format(GET_TOURNAMENT_URI, tournamentId))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class).returnResult().getResponseBody();

        var getTournamentResponse = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .readValue(getTournamentResponseAsString, CreateTournamentResponse.class);

        Assertions.assertNotNull(getTournamentResponse);
        Assertions.assertEquals(tournamentId, getTournamentResponse.getTournamentId());
        Assertions.assertEquals(matchIds, getTournamentResponse.getMatch().stream().map(MatchDetails::getMatchId).collect(Collectors.toList()));

        // Create Customer
        CreateCustomerResponse createCustomerResponse = webTestClient.post()
                                                                     .uri(CUSTOMERS_URI)
                                                                     .body(BodyInserters.fromPublisher(Mono.just(CreateCustomerRequest
                                                                                                                         .builder()
                                                                                                                         .firstName("John")
                                                                                                                         .lastName("Doe")
                                                                                                                         .dateOfBirth(LocalDate.now())
                                                                                                                         .build()), CreateCustomerRequest.class))
                                                                     .exchange()
                                                                     .expectStatus()
                                                                     .isCreated()
                                                                     .expectBody(CreateCustomerResponse.class).returnResult().getResponseBody();
        Assertions.assertNotNull(createCustomerResponse);
        Assertions.assertNotNull(createCustomerResponse.getCustomerId());

        // Update Customer licensed matches

        var updateLicenseResponse = webTestClient.patch()
                                                 .uri(String.format(PATCH_CUSTOMERS_LICENSED_URI, createCustomerResponse.getCustomerId()))
                                                 .body(BodyInserters.fromPublisher(Mono.just(UpdateLicenseRequest
                                                                                                     .builder()
                                                                                                     .data(matchIds)
                                                                                                     .build()), UpdateLicenseRequest.class))
                                                 .exchange()
                                                 .expectStatus()
                                                 .isAccepted()
                                                 .expectBody(UpdateLicenseResponse.class).returnResult().getResponseBody();
        Assertions.assertNotNull(updateLicenseResponse);
        Assertions.assertEquals(matchIds, updateLicenseResponse.getData());

        // Get Customer licensed matches

        var getCustomerLicensedMatches = webTestClient
                .get()
                .uri(String.format(GET_CUSTOMER_MATCHES_URI, createCustomerResponse.getCustomerId()))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class).returnResult().getResponseBody();

        var customerLicensedMatches = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .readValue(getCustomerLicensedMatches, CustomerMatchesResponse.class);

        Assertions.assertNotNull(customerLicensedMatches);
        Assertions.assertNotNull(customerLicensedMatches.getMeta());
        Assertions.assertNotNull(customerLicensedMatches.getData());
        Assertions.assertEquals(1, customerLicensedMatches.getMeta().getSize());
        Assertions.assertEquals(0, customerLicensedMatches.getMeta().getPage());
        Assertions.assertEquals(1, customerLicensedMatches.getData().size());
        Assertions.assertEquals(matchIds.get(0), customerLicensedMatches.getData().get(0).getMatchId());
        Assertions.assertEquals(matchStartDateTime, customerLicensedMatches.getData().get(0).getStartDateTime());
        Assertions.assertEquals(matchDuration, customerLicensedMatches.getData().get(0).getDurationInMinutes());
        Assertions.assertEquals(playerA, customerLicensedMatches.getData().get(0).getPlayerA());
        Assertions.assertEquals(playerB, customerLicensedMatches.getData().get(0).getPlayerB());
        Assertions.assertEquals(summary, customerLicensedMatches.getData().get(0).getSummary());
    }


}