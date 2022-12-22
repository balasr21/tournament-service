package com.img.sports.tournament.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.img.sports.tournament.converter.MatchConverter;
import com.img.sports.tournament.converter.TournamentConverter;
import com.img.sports.tournament.model.CreateTournamentRequest;
import com.img.sports.tournament.model.CreateTournamentResponse;
import com.img.sports.tournament.model.Match;
import com.img.sports.tournament.model.MatchDetails;
import com.img.sports.tournament.model.Tournament;
import com.img.sports.tournament.repository.MatchRepository;
import com.img.sports.tournament.repository.TournamentMatchesRepository;
import com.img.sports.tournament.repository.TournamentRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class TournamentServiceTest {

    @Mock
    TournamentRepository tournamentRepository;
    @Mock
    MatchRepository matchRepository;
    @Mock
    TournamentMatchesRepository tournamentMatchesRepository;
    @Mock
    TournamentConverter tournamentConverter;
    @Mock
    MatchConverter matchConverter;
    @InjectMocks
    TournamentService tournamentService;


    @Test
    void testCreateTournament() {
        var match = Match.builder().id(UUID.randomUUID()).durationInMinutes(2l).playerA("playerA").playerB("playerB").startDateTime(
                OffsetDateTime.now()).build();
        var matchDetails = MatchDetails.builder().matchId(match.getId()).playerB(
                match.getPlayerB()).playerA(match.getPlayerA()).durationInMinutes(2l).startDateTime(OffsetDateTime.now()).build();
        var tournament = Tournament.builder().id(UUID.randomUUID()).tournamentType(Tournament.TournamentType.TENNIS).startDateTime(OffsetDateTime.now()).durationInDays(2l).build();
        var response =
                CreateTournamentResponse.builder().tournamentId(tournament.getId()).tournamentType(CreateTournamentResponse.TournamentTypeEnum.TENNIS).duration(2l).startDateTime(OffsetDateTime.now())
                                        .match(List.of(matchDetails)).build();
        when(matchConverter.mapToMatch(any())).thenReturn(match);
        when(matchRepository.saveAll(anyList())).thenReturn(Flux.just(match));
        when(tournamentConverter.mapToTournamentEntity(any())).thenReturn(
                Tournament.builder().id(UUID.randomUUID()).tournamentType(Tournament.TournamentType.TENNIS).startDateTime(OffsetDateTime.now()).durationInDays(2l).build());
        when(tournamentRepository.save(any())).thenReturn(Mono.just(tournament));
        when(tournamentMatchesRepository.saveAll(anyList())).thenReturn(Flux.empty());
        when(tournamentConverter.mapToTournamentResponse(any(), anyList())).thenReturn(response);
        StepVerifier.create(tournamentService.createTournament(Mono.just(CreateTournamentRequest.builder().match(List.of(matchDetails)).build()))).expectNextMatches(createTournamentResponse -> {
            Assertions.assertNotNull(createTournamentResponse);
            Assertions.assertEquals(tournament.getId(), createTournamentResponse.getTournamentId());
            return true;
        }).verifyComplete();

    }

    @Test
    void testGetTournamentById() {
        var match = Match.builder().id(UUID.randomUUID()).durationInMinutes(2l).playerA("playerA").playerB("playerB").startDateTime(
                OffsetDateTime.now()).build();
        var matchDetails = MatchDetails.builder().matchId(match.getId()).playerB(
                match.getPlayerB()).playerA(match.getPlayerA()).durationInMinutes(2l).startDateTime(OffsetDateTime.now()).build();
        var tournament = Tournament.builder().id(UUID.randomUUID()).tournamentType(Tournament.TournamentType.TENNIS).startDateTime(OffsetDateTime.now()).durationInDays(2l).build();
        when(tournamentRepository.findById(any(UUID.class))).thenReturn(Mono.just(tournament));
        when(tournamentMatchesRepository.findAllMatchesByTournamentId(any())).thenReturn(Flux.just(match));
        when(tournamentConverter.mapToTournamentResponse(any(), anyList())).thenReturn(CreateTournamentResponse.builder().tournamentId(tournament.getId()).tournamentType(
                CreateTournamentResponse.TournamentTypeEnum.TENNIS).duration(2l).startDateTime(OffsetDateTime.now()).match(List.of(matchDetails)).build());
        StepVerifier.create(tournamentService.getTournamentById(tournament.getId())).expectNextMatches(createTournamentResponse -> {
            Assertions.assertNotNull(createTournamentResponse);
            Assertions.assertEquals(tournament.getId(), createTournamentResponse.getTournamentId());
            return true;
        }).verifyComplete();
    }


}