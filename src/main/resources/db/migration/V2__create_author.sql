-- Booking, können wir später löschen
create table if not exists booking (
                                       id          bigserial primary key,
                                       first_name  varchar(100) not null,
    last_name   varchar(100) not null,
    email       varchar(255) not null unique
    );


