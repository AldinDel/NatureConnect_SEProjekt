
-- Unique-Constraint auf name entfernen
ALTER TABLE nature_connect.equipment
    DROP CONSTRAINT IF EXISTS uq_equipment_name;

--  note-Spalte entfernen
ALTER TABLE nature_connect.equipment
    DROP COLUMN IF EXISTS note;
