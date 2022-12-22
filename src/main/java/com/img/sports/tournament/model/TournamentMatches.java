package com.img.sports.tournament.model;


import java.util.UUID;

import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@Builder
@Table("tournament_matches")
public class TournamentMatches {

    private UUID tournamentId;

    private UUID matchId;

}
