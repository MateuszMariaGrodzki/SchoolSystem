-- administrator
INSERT INTO application_user(id , first_name, last_name, phone_number, email, password, role)
VALUES (20345,'Admin', 'Admin', '000000000' , 'admin@admin.pl', '$2a$10$vvUHLe0gPvQfa1gDunO1WuRCAovr5HT34ebr1F79ZUBBkymt6Ztxi', 'ADMIN');

INSERT INTO administrator(id, application_user_id) VALUES (40532 , 20345);

INSERT INTO application_user(id,first_name, last_name, phone_number, email, password, role)
VALUES (12054,'Administrator', 'Great', '555555555' , 'admin@test.pl', '$2a$10$vvUHLe0gPvQfa1gDunO1WuRCAovr5HT34ebr1F79ZUBBkymt6Ztxi', 'ADMIN');

INSERT INTO administrator(id, application_user_id) VALUES (32145,12054);

INSERT INTO application_user(id , first_name, last_name, phone_number, email, password, role, is_expired)
VALUES (78345,'Deleted', 'Administrator', '000000000' , 'admindeleted@admin.pl', '$2a$10$vvUHLe0gPvQfa1gDunO1WuRCAovr5HT34ebr1F79ZUBBkymt6Ztxi', 'ADMIN', true);

INSERT INTO administrator(id, application_user_id) VALUES (4532 , 78345);

-- headmaster
INSERT INTO application_user(id , first_name, last_name, phone_number, email, password, role, is_expired)
VALUES (8796,'Deleted','Headmaster','111111111','deleted@master.pl','$2a$10$vvUHLe0gPvQfa1gDunO1WuRCAovr5HT34ebr1F79ZUBBkymt6Ztxi','HEADMASTER', true);

INSERT INTO headmaster(id, application_user_id) VALUES (452,8796);

INSERT INTO school(id,name,tier,city,street,post_code,building,headmaster_id)
VALUES (452,'Liceum imienia usuniętego mastera','HIGH','Warszawa','Ogniowa','88-666','8/1',452);

INSERT INTO application_user(id , first_name, last_name, phone_number, email, password, role)
VALUES (741,'Head','Master','111111111','head@master.pl','$2a$10$vvUHLe0gPvQfa1gDunO1WuRCAovr5HT34ebr1F79ZUBBkymt6Ztxi','HEADMASTER');

INSERT INTO headmaster(id, application_user_id) VALUES (321,741);

INSERT INTO school(id,name,tier,city,street,post_code,building,headmaster_id)
VALUES (321,'Liceum imienia Kopernika','HIGH','Lublin','Mickiewicza','88-666','8/1',321);


INSERT INTO application_user(id , first_name, last_name, phone_number, email, password, role)
VALUES (743,'Another','Headmaster','44444444444','head2@master.pl','$2a$10$vvUHLe0gPvQfa1gDunO1WuRCAovr5HT34ebr1F79ZUBBkymt6Ztxi','HEADMASTER');

INSERT INTO headmaster(id, application_user_id) VALUES (322,743);

INSERT INTO school(id,name,tier,city,street,post_code,building,headmaster_id)
VALUES (322,'Liceum imienia Kopernika2','PRIMARY','Lublin','Kunickiego','88-666','8/1',322);

-- teacher
INSERT INTO application_user(id, first_name , last_name , phone_number , email , password , role)
VALUES (56 , 'Teacher' , 'Gruszka' , '222222222' , 'teacher@gruszka.pl' , '$2a$10$vvUHLe0gPvQfa1gDunO1WuRCAovr5HT34ebr1F79ZUBBkymt6Ztxi', 'TEACHER');

INSERT INTO teacher(id, application_user_id,specialization) VALUES (86,56,'MATHEMATICS');

INSERT INTO application_user(id, first_name , last_name , phone_number , email , password , role , is_expired)
VALUES (598 , 'Deleted' , 'Student' , '222222222' , 'teacherdeleted@gruszka.pl' , '$2a$10$vvUHLe0gPvQfa1gDunO1WuRCAovr5HT34ebr1F79ZUBBkymt6Ztxi', 'TEACHER',true);

INSERT INTO teacher(id, application_user_id,specialization) VALUES (12,598,'MUSIC');

-- student
INSERT INTO application_user(id, first_name, last_name, phone_number, email, password, role)
VALUES (876, 'Trzezwy' , 'Student' , '333333333' , 'trzezwy@student.pl' , '$2a$10$vvUHLe0gPvQfa1gDunO1WuRCAovr5HT34ebr1F79ZUBBkymt6Ztxi' , 'STUDENT');

INSERT INTO student(id, application_user_id) VALUES(4786,876);

INSERT INTO application_user(id, first_name, last_name, phone_number, email, password, role, is_expired)
VALUES (15627, 'Deleted' , 'Student' , '333333333' , 'deletedstudent@student.pl' , '$2a$10$vvUHLe0gPvQfa1gDunO1WuRCAovr5HT34ebr1F79ZUBBkymt6Ztxi' , 'STUDENT' , true);

INSERT INTO student(id, application_user_id) VALUES(47869,15627);

