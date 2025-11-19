SET search_path TO nature_connect;

ALTER TABLE equipment
    ALTER COLUMN unit_price DROP NOT NULL;
