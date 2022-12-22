package com.img.sports.tournament.api.rest.resources;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.img.sports.tournament.api.TournamentApiDelegate;
import com.img.sports.tournament.model.CreateTournamentRequest;
import com.img.sports.tournament.model.CreateTournamentResponse;
import com.img.sports.tournament.service.TournamentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class TournamentController implements TournamentApiDelegate {

    private final TournamentService tournamentService;

    @Override
    public Mono<ResponseEntity<CreateTournamentResponse>> createTournament(Mono<CreateTournamentRequest> createTournamentRequest, ServerWebExchange exchange) {
        return tournamentService.createTournament(createTournamentRequest)
                                .flatMap(createTournamentResponse -> {
                                    return Mono.just(ResponseEntity.status(HttpStatus.CREATED).
                                                                   body(createTournamentResponse));
                                });
    }

    @Override
    public Mono<ResponseEntity<CreateTournamentResponse>> getTournamentById(UUID tournamentId, ServerWebExchange exchange) {
        return tournamentService.getTournamentById(tournamentId)
                                .flatMap(createTournamentResponse -> {
                                    return Mono.just(ResponseEntity.status(HttpStatus.OK).
                                                                   body(createTournamentResponse));
                                });
    }
}
