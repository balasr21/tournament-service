package com.img.sports.tournament.converter;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.core.Parameter;

import com.img.sports.tournament.model.Match;

import io.r2dbc.spi.Row;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class MatchReadWriteConverterTest {

    MatchReadConverter matchReadConverter;
    MatchWriteConverter matchWriteConverter;

    @BeforeEach
    void setUp() {
        matchReadConverter = new MatchReadConverter();
        matchWriteConverter = new MatchWriteConverter();
    }

    @Test
    void testConvertRowSourceToEntityModel() {
        Row source = mock(Row.class);

        var id = UUID.randomUUID();
        var playerA = "playerA";
        var playerB = "playerB";

        given(source.get("id", UUID.class)).willReturn(id);
        given(source.get("player_a", String.class)).willReturn(playerA);
        given(source.get("player_b", String.class)).willReturn(playerB);
        given(source.get("start_date_time", OffsetDateTime.class)).willReturn(OffsetDateTime.parse("2022-12-19T16:37:38.533Z"));
        given(source.get("duration_in_minutes", Long.class)).willReturn(20l);

        var match = matchReadConverter.convert(source);
        Assertions.assertAll(() -> Assertions.assertEquals(id.toString(), match.getId().toString()),
                             () -> Assertions.assertEquals(playerA, match.getPlayerA()),
                             () -> Assertions.assertEquals(playerB, match.getPlayerB()),
                             () -> Assertions.assertEquals(20l, match.getDurationInMinutes()),
                             () -> Assertions.assertEquals(OffsetDateTime.parse("2022-12-19T16:37:38.533Z"), match.getStartDateTime())

        );
    }

    @Test
    void testConvertEntityModelToOutboundRowSource() {
        var id = UUID.randomUUID();
        var playerA = "playerA";
        var playerB = "playerB";

        var match = Match.builder()
                         .id(id)
                         .playerA(playerA)
                         .playerB(playerB)
                         .durationInMinutes(20l)
                         .startDateTime(OffsetDateTime.parse("2022-12-19T17:37:38.533+01:00"))
                         .build();

        var matchRow = matchWriteConverter.convert(match);
        Assertions.assertAll(() -> Assertions.assertEquals(Parameter.from(id), matchRow.get("id")),
                             () -> Assertions.assertEquals(Parameter.from(playerA), matchRow.get("player_a")),
                             () -> Assertions.assertEquals(Parameter.from(playerB), matchRow.get("player_b")),
                             () -> Assertions.assertEquals(Parameter.from(20l), matchRow.get("duration_in_minutes")),
                             () -> Assertions.assertEquals(Parameter.from(OffsetDateTime.parse("2022-12-19T16:37:38.533Z")), matchRow.get("start_date_time"))

        );

    }

}