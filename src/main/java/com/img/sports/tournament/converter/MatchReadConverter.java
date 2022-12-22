package com.img.sports.tournament.converter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import com.img.sports.tournament.model.Match;

import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ReadingConverter
@Component
@Slf4j
@RequiredArgsConstructor
public class MatchReadConverter implements Converter<Row, Match> {

    public Match convert(Row source) {
        return Match
                .builder()
                .id(source.get("id", UUID.class))
                .playerA(source.get("player_a", String.class))
                .playerB(source.get("player_b", String.class))
                .startDateTime(getOffsetDateTimeField(source, "start_date_time"))
                .durationInMinutes(source.get("duration_in_minutes", Long.class))
                .build();
    }

    private <T> T getRowValue(Row source, String columnName, Class<T> clazz) {
        try {
            return source.get(columnName, clazz);
        } catch (IllegalArgumentException iae) {
            log.info("Column [{}] is not in query result.", columnName);
        }
        return null;
    }

    private OffsetDateTime getOffsetDateTimeField(Row source, String fieldName) {
        var value = getRowValue(source, fieldName, LocalDateTime.class);
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}

