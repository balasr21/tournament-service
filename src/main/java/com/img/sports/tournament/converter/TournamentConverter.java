package com.img.sports.tournament.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.img.sports.tournament.model.CreateTournamentRequest;
import com.img.sports.tournament.model.CreateTournamentResponse;
import com.img.sports.tournament.model.Match;
import com.img.sports.tournament.model.MatchDetails;
import com.img.sports.tournament.model.Tournament;

@Component
public class TournamentConverter {

    /**
     * maps create tournament request to Tournament entity
     *
     * @param tournamentRequest
     * @return
     */
    public Tournament mapToTournamentEntity(CreateTournamentRequest tournamentRequest) {
        return Tournament
                .builder()
                .tournamentType(mapTournamentType(tournamentRequest.getTournamentType()))
                .durationInDays(tournamentRequest.getDurationInDays())
                .startDateTime(DateHelper.mapLocalTimeToUTC(tournamentRequest.getStartDateTime()))
                .build();
    }

    private Tournament.TournamentType mapTournamentType(CreateTournamentRequest.TournamentTypeEnum tournamentType) {
        // Defaulting to TENNIS as it's the only supported type but enum can be expanded and switch case can be added to support
        return Tournament.TournamentType.TENNIS;
    }

    /**
     * maps tournament entity to create tournament response object
     *
     * @param tournament
     * @param matches
     * @return
     */
    public CreateTournamentResponse mapToTournamentResponse(Tournament tournament, List<Match> matches) {
        var matchDetails = matches
                .stream()
                .map(match -> MatchDetails
                        .builder()
                        .matchId(match.getId())
                        .startDateTime(DateHelper.mapUTCToLocalTime(match.getStartDateTime()))
                        .durationInMinutes(match.getDurationInMinutes())
                        .playerA(match.getPlayerA())
                        .playerB(match.getPlayerB())
                        .build())
                .collect(Collectors.toList());
        return CreateTournamentResponse
                .builder()
                .tournamentId(tournament.getId())
                .tournamentType(CreateTournamentResponse.TournamentTypeEnum.TENNIS)
                .duration(tournament.getDurationInDays())
                .startDateTime(tournament.getStartDateTime())
                .match(matchDetails).build();
    }

}
