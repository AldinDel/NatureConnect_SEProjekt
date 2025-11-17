SET search_path TO nature_connect;

ALTER TABLE event
ALTER COLUMN difficulty TYPE VARCHAR(20);

