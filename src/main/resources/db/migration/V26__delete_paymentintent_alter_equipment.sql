ALTER TABLE nature_connect.booking_equipment
ADD COLUMN invoiced BOOLEAN NOT NULL DEFAULT FALSE;

DROP TABLE IF EXISTS nature_connect.payment_intent CASCADE;