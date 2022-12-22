package com.img.sports.tournament.converter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import com.img.sports.tournament.model.Tournament;

import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ReadingConverter
@Component
@Slf4j
@RequiredArgsConstructor
public class TournamentReadConverter implements Converter<Row, Tournament> {

    public Tournament convert(Row source) {
        return Tournament
                .builder()
                .id(source.get("id", UUID.class))
                .durationInDays(source.get("duration_in_days", Long.class))
                .tournamentType(Tournament.TournamentType.TENNIS) // Need change here in mapping if we plan to use more different tournaments
                .startDateTime(getOffsetDateTimeField(source, "start_date_time"))
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
