ALTER TABLE nature_connect.customer_profile
    ADD COLUMN avatar_url VARCHAR(500);

ALTER TABLE nature_connect.customer_profile
    ADD COLUMN street VARCHAR(200);

ALTER TABLE nature_connect.customer_profile
    ADD COLUMN postal_code VARCHAR(20);

ALTER TABLE nature_connect.customer_profile
    ADD COLUMN city VARCHAR(100);

ALTER TABLE nature_connect.customer_profile
    ADD COLUMN country VARCHAR(100);
