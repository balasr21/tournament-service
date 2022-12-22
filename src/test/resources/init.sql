
DROP TABLE IF EXISTS tournament;
CREATE TABLE tournament
(
    id uuid NOT NULL DEFAULT RANDOM_UUID() PRIMARY KEY,
    tournament_type       varchar(255),
    start_date_time  TIMESTAMP NOT NULL,
    duration_in_days bigint NOT NULL
);

DROP TABLE IF EXISTS match;
CREATE TABLE match
(
    id uuid NOT NULL DEFAULT RANDOM_UUID() PRIMARY KEY,
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
    id uuid NOT NULL DEFAULT RANDOM_UUID() PRIMARY KEY,
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