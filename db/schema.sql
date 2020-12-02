create table account
(
    id    serial primary key,
    name  varchar,
    email varchar,
    password varchar
);

create table cinema_hall
(
    id         serial primary key,
    row        int4,
    col        int4,
    unique (row, col),
    account_id int4 references account (id),
    price float
);

CREATE EXTENSION pgcrypto;

insert into cinema_hall (row, col, price)
values (1, 1, 500),
       (1, 2, 500),
       (1, 3, 500),
       (2, 1, 500),
       (2, 2, 500),
       (2, 3, 500),
       (3, 1, 500),
       (3, 2, 500),
       (3, 3, 500);