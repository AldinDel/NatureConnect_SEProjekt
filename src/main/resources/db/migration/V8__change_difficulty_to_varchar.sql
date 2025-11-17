ALTER TABLE nature_connect.event
ALTER COLUMN difficulty TYPE VARCHAR(20) USING difficulty::text;

-- optional
ALTER TABLE nature_connect.event
    ADD CONSTRAINT chk_difficulty_values
        CHECK (difficulty IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED'));