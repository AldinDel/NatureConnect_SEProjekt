CREATE INDEX IF NOT EXISTS idx_booking_event_date_status
    ON nature_connect.booking (event_id, event_date, status);
