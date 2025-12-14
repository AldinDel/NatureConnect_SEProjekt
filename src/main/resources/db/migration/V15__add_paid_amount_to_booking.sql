-- Add paid_amount column to booking table for split invoice functionality
ALTER TABLE nature_connect.booking
ADD COLUMN paid_amount DECIMAL(10,2) DEFAULT 0.00;

-- Update existing bookings: set paid_amount to total_price if payment_status is PAID
UPDATE nature_connect.booking
SET paid_amount = total_price
WHERE payment_status = 'PAID';
