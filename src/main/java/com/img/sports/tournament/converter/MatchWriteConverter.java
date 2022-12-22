package com.img.sports.tournament.converter;

import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;

import com.img.sports.tournament.model.Match;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@WritingConverter
@Slf4j
@RequiredArgsConstructor
public class MatchWriteConverter implements Converter<Match, OutboundRow> {

    @Override
    public OutboundRow convert(Match source) {
        var row = new OutboundRow();
        Optional.ofNullable(source.getId()).ifPresent(v -> row.put("id", Parameter.from(v)));
        Optional.ofNullable(source.getPlayerA()).ifPresent(v -> row.put("player_a", Parameter.from(v)));
        Optional.ofNullable(source.getPlayerB()).ifPresent(v -> row.put("player_b", Parameter.from(v)));
        Optional.ofNullable(source.getDurationInMinutes()).ifPresent(v -> row.put("duration_in_minutes", Parameter.from(v)));
        Optional.ofNullable(source.getStartDateTime()).ifPresent(v -> {
            var x = v.atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime();
            row.put("start_date_time", Parameter.from(v.atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime()));
        });
        return row;
    }
}
