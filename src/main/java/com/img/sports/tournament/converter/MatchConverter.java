package com.img.sports.tournament.converter;


import org.springframework.stereotype.Component;

import com.img.sports.tournament.model.Match;
import com.img.sports.tournament.model.MatchDetails;

@Component
public class MatchConverter {

    /**
     * maps match details object to match entity
     * @param matchDetails
     * @return
     */
    public Match mapToMatch(MatchDetails matchDetails) {
        return Match
                .builder()
                .playerA(matchDetails.getPlayerA())
                .playerB(matchDetails.getPlayerB())
                .startDateTime(DateHelper.mapLocalTimeToUTC(matchDetails.getStartDateTime()))
                .durationInMinutes(matchDetails.getDurationInMinutes())
                .build();
    }

}
