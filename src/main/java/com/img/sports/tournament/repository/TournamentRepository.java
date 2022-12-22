package com.img.sports.tournament.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.img.sports.tournament.model.Tournament;

public interface TournamentRepository extends ReactiveCrudRepository<Tournament, UUID> {



}
