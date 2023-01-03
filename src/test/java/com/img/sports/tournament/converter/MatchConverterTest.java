package com.img.sports.tournament.converter;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.img.sports.tournament.model.MatchDetails;

import static org.junit.jupiter.api.Assertions.*;

class MatchConverterTest {

    @Test
    void testMapToMatchEntity_mapLocalTimeToUTC() {
        var matchConverter = new MatchConverter();
        var matchId = UUID.randomUUID();
        var matchResponse = matchConverter.mapToMatch(MatchDetails
                                                              .builder()
                                                              .playerA("playerA")
                                                              .playerB("playerB")
                                                              .startDateTime(OffsetDateTime.parse("2022-12-19T17:37:38.533+01:00"))
                                                              .matchId(matchId)
                                                              .build());
        Assertions.assertAll(() -> assertNotNull(matchResponse),
                             () -> assertEquals("playerA", matchResponse.getPlayerA()),
                             () -> assertEquals("playerB", matchResponse.getPlayerB()),
                             () -> assertEquals(OffsetDateTime.parse("2022-12-19T16:37:38.533Z"), matchResponse.getStartDateTime())
        );
    }

}