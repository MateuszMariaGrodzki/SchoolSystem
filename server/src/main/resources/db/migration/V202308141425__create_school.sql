CREATE TABLE school
(
    id SERIAL PRIMARY KEY,
    name varchar(255) NOT NULL,
    tier varchar(64) NOT NULL,
    city varchar(64) NOT NULL,
    street varchar(64) NOT NULL,
    post_code varchar(6) NOT NULL,
    building varchar(64) NOT NULL,
    headmaster_id bigint REFERENCES headmaster
);
