
CREATE SCHEMA IF NOT EXISTS nature_connect;

DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'difficulty_level') THEN
            CREATE TYPE difficulty_level AS ENUM ('BEGINNER','INTERMEDIATE','ADVANCED');
        END IF;
    END $$;

CREATE TABLE IF NOT EXISTS nature_connect.event (
                                            id               BIGSERIAL PRIMARY KEY,
                                            title            VARCHAR(200)      NOT NULL,
                                            description      TEXT,
                                            organizer        VARCHAR(200),
                                            category         VARCHAR(120),
                                            date             DATE              NOT NULL,
                                            start_time       TIME              NOT NULL,
                                            end_time         TIME              NOT NULL,
                                            location         VARCHAR(255)      NOT NULL,
                                            difficulty       difficulty_level  NOT NULL,
                                            min_participants INTEGER           NOT NULL DEFAULT 1,
                                            max_participants INTEGER           NOT NULL,
                                            price            NUMERIC(10,2)     NOT NULL DEFAULT 0,
                                            image_url        VARCHAR(500),
                                            created_at       TIMESTAMPTZ       NOT NULL DEFAULT NOW(),
                                            updated_at       TIMESTAMPTZ       NOT NULL DEFAULT NOW(),
                                            is_cancelled BOOLEAN NOT NULL DEFAULT FALSE,
                                            CONSTRAINT chk_participants_range CHECK (max_participants >= min_participants AND min_participants >= 1),
                                            CONSTRAINT chk_price_nonneg       CHECK (price >= 0),
                                            CONSTRAINT chk_time_order         CHECK (end_time > start_time)
);

CREATE OR REPLACE FUNCTION nature_connect.set_updated_at()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_event_updated_at ON nature_connect.event;
CREATE TRIGGER trg_event_updated_at
    BEFORE UPDATE ON nature_connect.event
    FOR EACH ROW EXECUTE FUNCTION nature_connect.set_updated_at();

CREATE INDEX IF NOT EXISTS idx_event_date           ON nature_connect.event (date);
CREATE INDEX IF NOT EXISTS idx_event_category       ON nature_connect.event (category);
CREATE INDEX IF NOT EXISTS idx_event_location       ON nature_connect.event (location);
CREATE INDEX IF NOT EXISTS idx_event_date_location  ON nature_connect.event (date, location);

CREATE TABLE IF NOT EXISTS nature_connect.equipment (
                                                id          BIGSERIAL PRIMARY KEY,
                                                name        VARCHAR(100) NOT NULL,
                                                rentable    BOOLEAN      NOT NULL DEFAULT TRUE,
                                                unit_price  NUMERIC(10,2) NOT NULL DEFAULT 0,
                                                stock       INTEGER,
                                                note        VARCHAR(200),
                                                CONSTRAINT uq_equipment_name UNIQUE (name),
                                                CONSTRAINT chk_equipment_price_nonneg CHECK (unit_price >= 0)
);

CREATE TABLE IF NOT EXISTS nature_connect.event_equipment (
                                                      event_id     BIGINT NOT NULL,
                                                      equipment_id BIGINT NOT NULL,
                                                      required     BOOLEAN NOT NULL DEFAULT FALSE,
                                                      PRIMARY KEY (event_id, equipment_id),
                                                      CONSTRAINT fk_event_equipment_event
                                                          FOREIGN KEY (event_id) REFERENCES nature_connect.event(id) ON DELETE CASCADE,
                                                      CONSTRAINT fk_event_equipment_equipment
                                                          FOREIGN KEY (equipment_id) REFERENCES nature_connect.equipment(id) ON DELETE RESTRICT
);
