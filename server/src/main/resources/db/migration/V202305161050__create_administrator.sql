CREATE TABLE IF NOT EXISTS administrator(
    id SERIAL PRIMARY KEY,
    application_user_id BIGINT NOT NULL,
    FOREIGN KEY (application_user_id) REFERENCES application_user
);

INSERT INTO application_user(first_name, last_name, phone_number, email, password, role)
VALUES ('Admin', 'Admin', '000000000' , 'Admin@admin.pl', '$2a$10$vvUHLe0gPvQfa1gDunO1WuRCAovr5HT34ebr1F79ZUBBkymt6Ztxi', 'ADMIN');

INSERT INTO administrator(application_user_id) VALUES (1);