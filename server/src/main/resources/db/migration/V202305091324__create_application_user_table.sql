CREATE TABLE IF NOT EXISTS application_user(
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(64) NOT NULL,
    last_name VARCHAR(64) NOT NULL,
    phone_number VARCHAR(32) NOT NULL,
    email VARCHAR(256) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
)