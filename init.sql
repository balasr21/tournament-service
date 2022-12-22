CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS tournament;
CREATE TABLE tournament
(
    id uuid PRIMARY KEY UNIQUE DEFAULT uuid_generate_v4(),
    tournament_type       varchar(255),
    start_date_time  TIMESTAMP NOT NULL,
    duration_in_days bigint NOT NULL
);

DROP TABLE IF EXISTS match;
CREATE TABLE match
(
    id uuid PRIMARY KEY UNIQUE DEFAULT uuid_generate_v4(),
    start_date_time  TIMESTAMP NOT NULL,
    duration_in_minutes bigint NOT NULL,
    player_a             varchar(255),
    player_b            varchar(255)
);

DROP TABLE IF EXISTS tournament_matches;
CREATE TABLE tournament_matches
(
    tournament_id       varchar(255),
    match_id            varchar(255)
);

DROP TABLE IF EXISTS customer;
CREATE TABLE customer
(
    id uuid PRIMARY KEY UNIQUE DEFAULT uuid_generate_v4(),
    first_name       varchar(255),
    last_name       varchar(255),
    date_of_birth  DATE NOT NULL,
    status varchar(255)
);

DROP TABLE IF EXISTS customer_licensed_matches;
CREATE TABLE customer_licensed_matches
(
    customer_id     varchar(255),
    license_id      varchar(255),
    match_id        varchar(255)
);