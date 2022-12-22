package com.img.sports.tournament.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.img.sports.tournament.model.Match;
import com.img.sports.tournament.model.TournamentMatches;

import reactor.core.publisher.Flux;

public interface TournamentMatchesRepository extends ReactiveCrudRepository<TournamentMatches, UUID> {

    @Query("select m.* from tournament_matches tm INNER JOIN match m on m.id=tm.match_id::uuid and tm.tournament_id=:tournamentId::varchar")
    Flux<Match> findAllMatchesByTournamentId(UUID tournamentId);

}
