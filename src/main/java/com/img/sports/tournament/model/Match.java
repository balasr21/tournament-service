package com.img.sports.tournament.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@Builder
@Table("match")
public class Match implements Persistable<UUID> {

    @Id
    private UUID id;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private OffsetDateTime startDateTime;

    private Long durationInMinutes;

    @Size(max = 255, message = "The property 'playerA' must be less than or equal to 255 characters.")
    private String playerA;

    @Size(max = 255, message = "The property 'playerB' must be less than or equal to 255 characters.")
    private String playerB;

    @Override
    public boolean isNew() {
        return true;
    }
}
