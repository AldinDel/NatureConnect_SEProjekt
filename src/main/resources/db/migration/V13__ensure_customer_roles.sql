SET search_path TO nature_connect;

-- Ensure all registered customers have the CUSTOMER role
-- This is a safety check for any customers that were registered with the wrong role due to bugs

DO $$
DECLARE
    customer_role_id BIGINT;
BEGIN
    -- Get the CUSTOMER role ID
    SELECT id INTO customer_role_id FROM role WHERE code = 'CUSTOMER';

    -- If CUSTOMER role exists, ensure all users with customer_profile have it
    IF customer_role_id IS NOT NULL THEN
        -- Insert CUSTOMER role for all users that have a customer_profile but don't have the CUSTOMER role yet
        INSERT INTO user_role (user_id, role_id)
        SELECT DISTINCT cp.user_id, customer_role_id
        FROM customer_profile cp
        WHERE cp.user_id IS NOT NULL
          AND NOT EXISTS (
            SELECT 1 FROM user_role ur
            WHERE ur.user_id = cp.user_id
              AND ur.role_id = customer_role_id
        );
    END IF;
END $$;
