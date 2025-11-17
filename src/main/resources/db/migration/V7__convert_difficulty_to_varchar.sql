-- ============================================
-- Migration: Convert difficulty from ENUM to VARCHAR
-- Reason: Hibernate compatibility
-- ============================================

SET search_path TO nature_connect;

-- Schritt 1: Constraint temporär entfernen (falls vorhanden)
ALTER TABLE event
    ALTER COLUMN difficulty DROP NOT NULL;

-- Schritt 2: Spalte zu VARCHAR konvertieren
ALTER TABLE event
ALTER COLUMN difficulty TYPE VARCHAR(20)
    USING difficulty::text;

-- Schritt 3: NOT NULL wieder setzen
ALTER TABLE event
    ALTER COLUMN difficulty SET NOT NULL;

-- Schritt 4: Optional - ENUM Type löschen (vorsichtig!)
-- Nur ausführen, wenn nichts anderes den Typ nutzt
-- DROP TYPE IF EXISTS difficulty_level CASCADE;

-- Verifizierung
-- SELECT column_name, data_type, is_nullable
-- FROM information_schema.columns
-- WHERE table_schema = 'nature_connect'
--   AND table_name = 'event'
--   AND column_name = 'difficulty';