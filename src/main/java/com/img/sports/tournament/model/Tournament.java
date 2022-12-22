package com.img.sports.tournament.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.With;

@Data
@ToString
@AllArgsConstructor
@Builder
@Table("tournament")
public class Tournament implements Persistable<UUID> {

    @Id
    private UUID id;

    @NotNull
    private TournamentType tournamentType;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private OffsetDateTime startDateTime;

    private Long durationInDays;

    @With(AccessLevel.PRIVATE)
    @Transient
    @JsonIgnore
    private boolean newProduct;

    @Override
    public boolean isNew() {
        return this.newProduct || id == null;
    }

    public enum TournamentType {
        TENNIS
    }

}
