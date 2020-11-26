create table account (
                         id serial primary key,
                         name varchar,
                         email varchar
);

create table hall (
    id serial primary key,
    row int4,
    col int4,
    unique (row, col),
    account_id int4 references account(id)
);

insert into hall (row, col) values
    (1, 1),
    (1, 2),
    (1, 3),
    (2, 1),
    (2, 2),
    (2, 3),
    (3, 1),
    (3, 2),
    (3, 3);