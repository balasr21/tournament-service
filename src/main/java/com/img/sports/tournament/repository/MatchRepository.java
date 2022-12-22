package com.img.sports.tournament.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.img.sports.tournament.model.Match;

public interface MatchRepository extends ReactiveCrudRepository<Match, UUID> {

}
