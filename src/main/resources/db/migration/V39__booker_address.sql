SET search_path TO nature_connect;

ALTER TABLE booking
    ADD COLUMN IF NOT EXISTS booker_address VARCHAR(200);