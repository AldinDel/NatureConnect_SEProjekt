CREATE TABLE IF NOT EXISTS nature_connect.event_hike_route
(
    event_id BIGINT      NOT NULL,
    hike_key VARCHAR(80) NOT NULL,
    PRIMARY KEY (event_id, hike_key),
    CONSTRAINT fk_event_hike_route_event
        FOREIGN KEY (event_id)
            REFERENCES nature_connect.event (id)
            ON DELETE CASCADE
);
