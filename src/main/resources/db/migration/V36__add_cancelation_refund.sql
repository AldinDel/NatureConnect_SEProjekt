SET search_path TO nature_connect;

ALTER TABLE booking
    ADD COLUMN IF NOT EXISTS refund_amount NUMERIC(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS cancellation_deadline TIMESTAMPTZ;


ALTER TABLE event
    ADD COLUMN IF NOT EXISTS cancellation_reason TEXT,
    ADD COLUMN IF NOT EXISTS cancellation_deadline DATE;