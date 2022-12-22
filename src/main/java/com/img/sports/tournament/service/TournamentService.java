package com.img.sports.tournament.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.img.sports.tournament.converter.MatchConverter;
import com.img.sports.tournament.converter.TournamentConverter;
import com.img.sports.tournament.model.CreateTournamentRequest;
import com.img.sports.tournament.model.CreateTournamentResponse;
import com.img.sports.tournament.model.Match;
import com.img.sports.tournament.model.TournamentMatches;
import com.img.sports.tournament.repository.MatchRepository;
import com.img.sports.tournament.repository.TournamentMatchesRepository;
import com.img.sports.tournament.repository.TournamentRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;
    private final TournamentMatchesRepository tournamentMatchesRepository;
    private final TournamentConverter tournamentConverter;
    private final MatchConverter matchConverter;


    @Transactional
    public Mono<CreateTournamentResponse> createTournament(Mono<CreateTournamentRequest> createTournamentRequest) {

        return createTournamentRequest
                .flatMap(tournamentRequest -> {
                    var matches = tournamentRequest.getMatch().stream().map(matchConverter::mapToMatch).collect(Collectors.toList());
                    return Mono.zip(matchRepository.saveAll(matches).collectList(), Mono.just(tournamentRequest));
                }).flatMap(tuple -> {
                    var tournament = tournamentConverter.mapToTournamentEntity(tuple.getT2());
                    return Mono.zip(tournamentRepository.save(tournament), Mono.just(tuple.getT1()));
                })
                .flatMap(tuple -> {
                    var tournamentId = tuple.getT1().getId();
                    List<Match> matchIds = tuple.getT2();
                    var tournamentMatches = matchIds.stream().map(matchId -> TournamentMatches.builder().tournamentId(tournamentId).matchId(matchId.getId()).build()).collect(Collectors.toList());
                    return Mono.just(tuple).delayUntil(response -> tournamentMatchesRepository.saveAll(tournamentMatches));
                })
                .flatMap(tuple -> Mono.just(tournamentConverter.mapToTournamentResponse(tuple.getT1(), tuple.getT2())))
                .doOnSuccess(s -> log.debug("Successfully created tournament [{}] with number of matches [{}]", s.getTournamentId(), s.getMatch().size()))
                .doOnError(throwable -> {
                    log.error("Error during creating Tournament [{}]", throwable);
                });
    }

    public Mono<CreateTournamentResponse> getTournamentById(UUID tournamentId) {
        return tournamentMatchesRepository.findAllMatchesByTournamentId(tournamentId)
                                          .collectList()
                                          .flatMap(matches -> Mono.zip(tournamentRepository.findById(tournamentId), Mono.just(matches)))
                                          .flatMap(tuple -> Mono.just(tournamentConverter.mapToTournamentResponse(tuple.getT1(), tuple.getT2())))
                                          .doOnSuccess(s -> log.debug("Successfully retrieved tournament [{}] with number of matches [{}]", tournamentId,
                                                                      CollectionUtils.isEmpty(s.getMatch()) ? 0 : s.getMatch().size()))
                                          .doOnError(throwable -> log.error("Error [{}] during retrieving Tournament details for tournamentId [{}]", throwable, tournamentId));
    }

}
