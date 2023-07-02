INSERT INTO application_user(id,first_name, last_name, phone_number, email, password, role)
VALUES (12054,'Administrator', 'Great', '555555555' , 'admin@test.pl', '$2a$10$vvUHLe0gPvQfa1gDunO1WuRCAovr5HT34ebr1F79ZUBBkymt6Ztxi', 'ADMIN');

INSERT INTO administrator(id, application_user_id) VALUES (32145,12054);