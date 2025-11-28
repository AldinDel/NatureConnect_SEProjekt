SET search_path TO nature_connect;

-- Step 1: Add new roles FRONT and CUSTOMER if they don't exist
INSERT INTO role (code)
VALUES ('FRONT'), ('CUSTOMER')
ON CONFLICT (code) DO NOTHING;

-- Step 2: Update any users with FO_STAFF role to FRONT
-- First, get the role IDs
DO $$
DECLARE
    old_fo_staff_id BIGINT;
    old_fo_user_id BIGINT;
    new_front_id BIGINT;
    new_customer_id BIGINT;
BEGIN
    -- Get old role IDs
    SELECT id INTO old_fo_staff_id FROM role WHERE code = 'FO_STAFF';
    SELECT id INTO old_fo_user_id FROM role WHERE code = 'FO_USER';

    -- Get new role IDs
    SELECT id INTO new_front_id FROM role WHERE code = 'FRONT';
    SELECT id INTO new_customer_id FROM role WHERE code = 'CUSTOMER';

    -- Update user_role mappings: FO_STAFF -> FRONT
    IF old_fo_staff_id IS NOT NULL AND new_front_id IS NOT NULL THEN
        UPDATE user_role
        SET role_id = new_front_id
        WHERE role_id = old_fo_staff_id;
    END IF;

    -- Update user_role mappings: FO_USER -> CUSTOMER
    IF old_fo_user_id IS NOT NULL AND new_customer_id IS NOT NULL THEN
        UPDATE user_role
        SET role_id = new_customer_id
        WHERE role_id = old_fo_user_id;
    END IF;
END $$;

-- Step 3: Delete old roles FO_STAFF and FO_USER
DELETE FROM role WHERE code IN ('FO_STAFF', 'FO_USER');
