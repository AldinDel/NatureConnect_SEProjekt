SET search_path TO nature_connect;

ALTER TABLE booking
    ADD COLUMN IF NOT EXISTS hike_route_key VARCHAR(50);
