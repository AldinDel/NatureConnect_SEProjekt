-- 1) Spalte hinzufügen (nullable)
ALTER TABLE nature_connect.booking
    ADD COLUMN event_date DATE;

-- 2) Bestehende Bookings befüllen
UPDATE nature_connect.booking b
SET event_date = e.date
    FROM nature_connect.event e
WHERE b.event_id = e.id
  AND b.event_date IS NULL;

-- 3) Constraint setzen
ALTER TABLE nature_connect.booking
    ALTER COLUMN event_date SET NOT NULL;

-- 4) Index für spätere Queries
CREATE INDEX idx_booking_event_date
    ON nature_connect.booking (event_id, event_date);
