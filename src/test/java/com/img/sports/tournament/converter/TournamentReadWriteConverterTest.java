package com.img.sports.tournament.converter;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.core.Parameter;

import com.img.sports.tournament.model.Tournament;

import io.r2dbc.spi.Row;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class TournamentReadWriteConverterTest {

    TournamentReadConverter tournamentReadConverter;
    TournamentWriteConverter tournamentWriteConverter;

    @BeforeEach
    void setUp() {
        tournamentReadConverter = new TournamentReadConverter();
        tournamentWriteConverter = new TournamentWriteConverter();
    }

    @Test
    void testConvertRowSourceToEntity() {

        Row source = mock(Row.class);
        var id = UUID.randomUUID();

        given(source.get("id", UUID.class)).willReturn(id);
        given(source.get("duration_in_days", Long.class)).willReturn(2l);
        given(source.get("start_date_time", OffsetDateTime.class)).willReturn(OffsetDateTime.parse("2022-12-19T16:37:38.533Z"));
        var tournamentResponse = tournamentReadConverter.convert(source);
        Assertions.assertAll(() -> Assertions.assertEquals(id.toString(), tournamentResponse.getId().toString()),
                             () -> Assertions.assertEquals(Tournament.TournamentType.TENNIS, tournamentResponse.getTournamentType()),
                             () -> Assertions.assertEquals(2l, tournamentResponse.getDurationInDays()),
                             () -> Assertions.assertEquals(OffsetDateTime.parse("2022-12-19T16:37:38.533Z"), tournamentResponse.getStartDateTime()));
    }

    @Test
    void testConvertEntityToOutboundRow() {

        var id = UUID.randomUUID();
        var tournament = Tournament.builder().id(id).tournamentType(Tournament.TournamentType.TENNIS).durationInDays(2l).startDateTime(OffsetDateTime.parse("2022-12-19T17:37:38.533+01:00")).build();

        var tournamentResponse = tournamentWriteConverter.convert(tournament);

        Assertions.assertAll(() -> Assertions.assertEquals(Parameter.from(id), tournamentResponse.get("id")),
                             () -> Assertions.assertEquals(Parameter.from(2l), tournamentResponse.get("duration_in_days")),
                             () -> Assertions.assertEquals(Parameter.from(OffsetDateTime.parse("2022-12-19T16:37:38.533Z")), tournamentResponse.get("start_date_time")));
    }

}