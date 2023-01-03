package com.img.sports.tournament.converter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.img.sports.tournament.model.CreateTournamentRequest;
import com.img.sports.tournament.model.CreateTournamentResponse;
import com.img.sports.tournament.model.Match;
import com.img.sports.tournament.model.Tournament;

import static org.junit.jupiter.api.Assertions.*;

class TournamentConverterTest {

    TournamentConverter tournamentConverter;

    @BeforeEach
    void setUp() {
        tournamentConverter = new TournamentConverter();
    }

    @Test
    void testMapTournamentEntityResponse() {
        var tournamentResponse = tournamentConverter.mapToTournamentEntity(CreateTournamentRequest
                                                                                   .builder()
                                                                                   .tournamentType(CreateTournamentRequest.TournamentTypeEnum.TENNIS)
                                                                                   .startDateTime(OffsetDateTime.parse("2022-12-19T17:37:38.533+01:00"))
                                                                                   .durationInDays(2l)
                                                                                   .build());
        Assertions.assertNotNull(tournamentResponse);
        Assertions.assertAll(() -> assertNotNull(tournamentResponse),
                             () -> assertEquals(Tournament.TournamentType.TENNIS, tournamentResponse.getTournamentType()),
                             () -> assertEquals(2, tournamentResponse.getDurationInDays()),
                             () -> assertEquals(OffsetDateTime.parse("2022-12-19T16:37:38.533Z"), tournamentResponse.getStartDateTime())
        );
    }

    @Test
    void testMapTournamentResponse() {
        var playerA = "playerA";
        var playerB = "playerB";
        var firstMatchId = UUID.randomUUID();
        var secondMatchId = UUID.randomUUID();
        var matches = List.of(Match.builder()
                                   .id(firstMatchId)
                                   .playerA(playerA)
                                   .playerB(playerB)
                                   .durationInMinutes(20l)
                                   .startDateTime(OffsetDateTime.parse("2022-12-19T16:37:38.533Z"))
                                   .build(),
                              Match.builder()
                                   .id(secondMatchId)
                                   .playerA(playerA)
                                   .playerB(playerB)
                                   .durationInMinutes(40l)
                                   .startDateTime(OffsetDateTime.parse("2022-12-20T16:37:38.533Z"))
                                   .build());

        var tournamentResponse = tournamentConverter.mapToTournamentResponse(
                Tournament
                        .builder()
                        .tournamentType(Tournament.TournamentType.TENNIS)
                        .durationInDays(60l)
                        .startDateTime(OffsetDateTime.parse("2022-12-19T16:37:38.533Z"))
                        .build(), matches);
        Assertions.assertAll(() -> assertNotNull(tournamentResponse),
                             () -> assertEquals(CreateTournamentResponse.TournamentTypeEnum.TENNIS, tournamentResponse.getTournamentType()),
                             () -> assertEquals(60l, tournamentResponse.getDuration()),
                             () -> assertEquals(OffsetDateTime.parse("2022-12-19T16:37:38.533Z"), tournamentResponse.getStartDateTime()),
                             () -> assertEquals(2, tournamentResponse.getMatch().size()),
                             () -> assertEquals(firstMatchId.toString(), tournamentResponse.getMatch().get(0).getMatchId().toString()),
                             () -> assertEquals(playerA, tournamentResponse.getMatch().get(0).getPlayerA()),
                             () -> assertEquals(playerB, tournamentResponse.getMatch().get(0).getPlayerB()),
                             () -> assertEquals(20l, tournamentResponse.getMatch().get(0).getDurationInMinutes()),
                             () -> assertEquals(OffsetDateTime.parse("2022-12-19T16:37:38.533Z"), tournamentResponse.getMatch().get(0).getStartDateTime()),
                             () -> assertEquals(secondMatchId.toString(), tournamentResponse.getMatch().get(1).getMatchId().toString()),
                             () -> assertEquals(playerA, tournamentResponse.getMatch().get(1).getPlayerA()),
                             () -> assertEquals(playerB, tournamentResponse.getMatch().get(1).getPlayerB()),
                             () -> assertEquals(OffsetDateTime.parse("2022-12-20T16:37:38.533Z"), tournamentResponse.getMatch().get(1).getStartDateTime()),
                             () -> assertEquals(40l, tournamentResponse.getMatch().get(1).getDurationInMinutes())
        );
    }

}