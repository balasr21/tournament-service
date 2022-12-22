package com.img.sports.tournament.api.rest.resources;

import java.time.ZoneId;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class TimeZoneConfig {

    public static final String ZONE_ID = "Asia/Calcutta";

    @PostConstruct
    public void init() {
        // Setting Spring Boot SetTimeZone
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(ZONE_ID)));
    }

}