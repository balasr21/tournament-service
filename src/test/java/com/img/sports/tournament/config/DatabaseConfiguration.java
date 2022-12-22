package com.img.sports.tournament.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import com.img.sports.tournament.converter.MatchReadConverter;
import com.img.sports.tournament.converter.MatchWriteConverter;
import com.img.sports.tournament.converter.TournamentReadConverter;
import com.img.sports.tournament.converter.TournamentWriteConverter;

import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;


@TestConfiguration
@EnableR2dbcRepositories
public class DatabaseConfiguration extends AbstractR2dbcConfiguration {

    @Value("${spring.r2dbc.database}")
    private String database;
    @Value("${spring.r2dbc.init.script}")
    private String initScript;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        return H2ConnectionFactory.inMemory(database);
    }

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource(initScript)));
        initializer.setDatabasePopulator(populator);
        return initializer;
    }


    @Bean
    @Override
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converterList = List.of(new TournamentReadConverter(), new MatchReadConverter(), new TournamentWriteConverter(), new MatchWriteConverter());
        return new R2dbcCustomConversions(getStoreConversions(), converterList);
    }



}