package com.img.sports.tournament;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(scanBasePackages = "com.img")
@Slf4j
public class TournamentApplication {

    public static void main(String[] args) {
        SpringApplication.run(TournamentApplication.class, args);
    }

}
