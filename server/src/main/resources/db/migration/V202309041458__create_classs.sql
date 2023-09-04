create table classs
(
    id SERIAL PRIMARY KEY,
    profile varchar(64) NOT NULL,
    supervising_teacher bigint references teacher
);

alter table student add column classs_id int references classs(id)
