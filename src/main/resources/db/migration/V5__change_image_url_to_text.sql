-- Change image_url column to TEXT to support base64
ALTER TABLE nature_connect.event ALTER COLUMN image_url TYPE TEXT;