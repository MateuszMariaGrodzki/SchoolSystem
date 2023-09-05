create table classs
(
    id SERIAL PRIMARY KEY,
    profile varchar(64) NOT NULL,
    teacher_id bigint references teacher
);

alter table student add column classs_id int references classs(id)
