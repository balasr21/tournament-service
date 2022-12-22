package com.img.sports.tournament.converter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public final class DateHelper {

    /**
     * Converts UTC to current zone timestamp. All the response will be requesting zone specific
     * @param startDateTime
     * @return
     */
    public static OffsetDateTime mapUTCToLocalTime(OffsetDateTime startDateTime) {
        return startDateTime.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime();
    }

    /**
     * Converting Current zone timestamp to UTC. All saves to databse will be saved in UTC
     *
     * @param startDateTime
     * @return
     */
    public static OffsetDateTime mapLocalTimeToUTC(OffsetDateTime startDateTime) {
        return startDateTime.atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime();
    }

}
