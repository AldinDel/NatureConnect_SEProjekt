-- Add paid_amount column to booking table for split invoice functionality
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'nature_connect'
        AND table_name = 'booking'
        AND column_name = 'paid_amount'
    ) THEN
        ALTER TABLE nature_connect.booking
        ADD COLUMN paid_amount DECIMAL(10,2) DEFAULT 0.00;
    END IF;
END $$;

-- Update existing bookings: set paid_amount to total_price if payment_status is PAID
UPDATE nature_connect.booking
SET paid_amount = total_price
WHERE payment_status = 'PAID' AND (paid_amount IS NULL OR paid_amount = 0);
