CREATE TABLE IF NOT EXISTS headmaster(
    id SERIAL PRIMARY KEY,
    application_user_id BIGINT NOT NULL,
    FOREIGN KEY (application_user_id) REFERENCES application_user
);