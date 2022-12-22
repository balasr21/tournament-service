package com.img.sports.tournament.converter;

import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;

import com.img.sports.tournament.model.Tournament;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@WritingConverter
@Slf4j
@RequiredArgsConstructor
public class TournamentWriteConverter implements Converter<Tournament, OutboundRow> {

    @Override
    public OutboundRow convert(Tournament source) {
        var row = new OutboundRow();
        Optional.ofNullable(source.getId()).ifPresent(v -> row.put("id", Parameter.from(v)));
        Optional.ofNullable(source.getTournamentType()).ifPresent(v -> row.put("tournament_type", Parameter.from(v)));
        Optional.ofNullable(source.getDurationInDays()).ifPresent(v -> row.put("duration_in_days", Parameter.from(v)));
        Optional.ofNullable(source.getStartDateTime()).ifPresent(v -> row.put("start_date_time", Parameter.from(v.atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime())));
        return row;
    }
}
