SET search_path TO nature_connect;

ALTER TABLE event
    ADD COLUMN audience VARCHAR(50);
