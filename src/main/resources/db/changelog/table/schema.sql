create table users
(
    id serial primary key,
    login varchar(50) not null unique,
    password varchar(100) not null
);

create table files
(
    id serial primary key,
    file_name varchar(50),
    type varchar(10) not null ,
    content bytea not null,
    create_date timestamp,
    size bigint not null,
    user_id serial,
    CONSTRAINT fk_user_file FOREIGN KEY (user_id)
        REFERENCES users (id) MATCH SIMPLE
);

insert into users(login,password)
values ('user@gmail.com','password');