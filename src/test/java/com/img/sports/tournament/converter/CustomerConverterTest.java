package com.img.sports.tournament.converter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.img.sports.tournament.model.CreateCustomerRequest;
import com.img.sports.tournament.model.Customer;
import com.img.sports.tournament.model.Match;
import com.img.sports.tournament.model.SummaryEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import reactor.util.function.Tuples;

class CustomerConverterTest {

    CustomerConverter converter;

    @BeforeEach
    void setUp() {
        converter = new CustomerConverter();
    }

    @Test
    void testMapCustomerToEntity() {
        var dob = LocalDate.now().minusYears(30);
        var customerEntity = converter.mapCustomerToEntity(CreateCustomerRequest.builder().firstName("John").lastName("Doe").dateOfBirth(dob).build());
        Assertions.assertNotNull(customerEntity);
        Assertions.assertAll(() -> assertEquals(dob, customerEntity.getDateOfBirth()),
                             () -> assertEquals("John", customerEntity.getFirstName()),
                             () -> assertEquals("Doe", customerEntity.getLastName()),
                             () -> assertEquals(Customer.Status.ACTIVE, customerEntity.getStatus()));
    }

    @Test
    void testEntityToCustomerResponse() {
        var customerId = UUID.randomUUID();
        var dob = LocalDate.now().minusYears(30);
        var firstName = "John";
        var lastName = "Doe";
        var customerResponse = converter.mapToCustomerResponse(Customer.builder().id(customerId).dateOfBirth(dob).firstName(firstName).lastName(lastName).status(Customer.Status.ACTIVE).build());
        Assertions.assertNotNull(customerResponse);
        Assertions.assertAll(() -> assertEquals(dob, customerResponse.getDateOfBirth()),
                             () -> assertEquals(firstName, customerResponse.getFirstName()),
                             () -> assertEquals(lastName, customerResponse.getLastName()),
                             () -> assertEquals(customerId, customerResponse.getCustomerId()));

    }

    @Test
    void testMapToCustomerLicenseResponse() {
        var playerA = "playerA";
        var playerB = "playerB";
        var firstMatchId = UUID.randomUUID();
        var secondMatchId = UUID.randomUUID();
        var licenseRequest = Tuples.of(UUID.randomUUID(),
                                       List.of(Match.builder()
                                                    .id(firstMatchId)
                                                    .playerA(playerA)
                                                    .playerB(playerB)
                                                    .durationInMinutes(20l)
                                                    .startDateTime(OffsetDateTime.now().plusDays(2))
                                                    .build(),
                                               Match.builder()
                                                    .id(secondMatchId)
                                                    .playerA(playerA)
                                                    .playerB(playerB)
                                                    .durationInMinutes(40l)
                                                    .startDateTime(OffsetDateTime.now().plusDays(4))
                                                    .build()));
        var licenseResponse = converter.mapToCustomerLicenseResponse(licenseRequest);
        Assertions.assertAll(() -> assertEquals(licenseRequest.getT1().toString(), licenseResponse.getLicenseId()),
                             () -> assertNotNull(licenseResponse.getData()),
                             () -> assertEquals(2, licenseResponse.getData().size()),
                             () -> assertEquals(firstMatchId.toString(), licenseResponse.getData().get(0).toString()),
                             () -> assertEquals(secondMatchId.toString(), licenseResponse.getData().get(1).toString())
        );
    }

    @Test
    void testMapCustomerToMatchDetails_With_Summary_As_AvB() {
        var playerA = "playerA";
        var playerB = "playerB";
        var firstMatchId = UUID.randomUUID();
        var secondMatchId = UUID.randomUUID();
        var matches = List.of(Match.builder()
                                   .id(firstMatchId)
                                   .playerA(playerA)
                                   .playerB(playerB)
                                   .durationInMinutes(20l)
                                   .startDateTime(OffsetDateTime.now().plusDays(2))
                                   .build(),
                              Match.builder()
                                   .id(secondMatchId)
                                   .playerA(playerA)
                                   .playerB(playerB)
                                   .durationInMinutes(40l)
                                   .startDateTime(OffsetDateTime.now().plusDays(4))
                                   .build());
        var response = converter.mapCustomerToMatchDetails(matches, SummaryEnum.AvB);
        Assertions.assertAll(() -> assertNotNull(response.getMeta()),
                             () -> assertNotNull(response.getData()),
                             () -> assertEquals(2l, response.getMeta().getSize()),
                             () -> assertEquals(firstMatchId.toString(), response.getData().get(0).getMatchId().toString()),
                             () -> assertEquals(playerA, response.getData().get(0).getPlayerA()),
                             () -> assertEquals(playerB, response.getData().get(0).getPlayerB()),
                             () -> assertEquals("playerA vs playerB", response.getData().get(0).getSummary()),
                             () -> assertEquals(secondMatchId.toString(), response.getData().get(1).getMatchId().toString()),
                             () -> assertEquals(playerA, response.getData().get(1).getPlayerA()),
                             () -> assertEquals(playerB, response.getData().get(1).getPlayerB()),
                             () -> assertEquals("playerA vs playerB", response.getData().get(1).getSummary())
        );
    }

    @Test
    void testMapCustomerToMatchDetails_With_Summary_As_AvBTime() {
        var playerA = "playerA";
        var playerB = "playerB";
        var firstMatchId = UUID.randomUUID();
        var secondMatchId = UUID.randomUUID();
        var matches = List.of(Match.builder()
                                   .id(firstMatchId)
                                   .playerA(playerA)
                                   .playerB(playerB)
                                   .durationInMinutes(20l)
                                   .startDateTime(OffsetDateTime.now().plusHours(1))
                                   .build(),
                              Match.builder()
                                   .id(secondMatchId)
                                   .playerA(playerA)
                                   .playerB(playerB)
                                   .durationInMinutes(40l)
                                   .startDateTime(OffsetDateTime.now().minusDays(2))
                                   .build());
        var response = converter.mapCustomerToMatchDetails(matches, SummaryEnum.AvBTime);
        Assertions.assertAll(() -> assertNotNull(response.getMeta()),
                             () -> assertNotNull(response.getData()),
                             () -> assertEquals(2l, response.getMeta().getSize()),
                             () -> assertEquals(firstMatchId.toString(), response.getData().get(0).getMatchId().toString()),
                             () -> assertEquals(playerA, response.getData().get(0).getPlayerA()),
                             () -> assertEquals(playerB, response.getData().get(0).getPlayerB()),
                             () -> assertTrue(response.getData().get(0).getSummary().matches("playerA vs playerB starts in [0-9]{1,5} minutes")),
                             () -> assertEquals(secondMatchId.toString(), response.getData().get(1).getMatchId().toString()),
                             () -> assertEquals(playerA, response.getData().get(1).getPlayerA()),
                             () -> assertEquals(playerB, response.getData().get(1).getPlayerB()),
                             () -> assertTrue(response.getData().get(1).getSummary().matches("playerA vs playerB started [0-9]{1,5} minutes ago"))
        );
    }

}