--bsp--
create table if not exists events (
                                      id              bigserial primary key,
                                      title           varchar(200) not null,
    description     text,
    category        varchar(80),
    activity_type   varchar(80),
    location        varchar(160),
    event_date      date not null,
    start_time      time,
    end_time        time,
    capacity        int not null check (capacity >= 0),
    available_seats int not null check (available_seats >= 0),
    price_cents     int not null default 0 check (price_cents >= 0),
    created_at      timestamp not null default current_timestamp
    );

