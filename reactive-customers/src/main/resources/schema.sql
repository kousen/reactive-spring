create table customer
(
    id         long generated always as identity primary key,
    first_name varchar(20) not null,
    last_name  varchar(20) not null
);