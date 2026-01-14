set search_path TO nature_connect;

ALTER TABLE nature_connect.event
    ADD COLUMN IF NOT EXISTS end_date DATE;
