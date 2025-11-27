SET search_path TO nature_connect;

-- User accounts table
CREATE TABLE IF NOT EXISTS user_account (
                                            id             BIGSERIAL PRIMARY KEY,
                                            email          VARCHAR(200) NOT NULL, -- case-insensitive uniqueness enforced via functional index below
                                            password_hash  VARCHAR(255) NOT NULL,
                                            first_name     VARCHAR(100),
                                            last_name      VARCHAR(100),
                                            is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
                                            created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                                            updated_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Roles
CREATE TABLE IF NOT EXISTS role (
                                    id   BIGSERIAL PRIMARY KEY,
                                    code VARCHAR(50) NOT NULL UNIQUE
);

-- Many-to-many assignment of users to roles
CREATE TABLE IF NOT EXISTS user_role (
                                         user_id BIGINT NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
                                         role_id BIGINT NOT NULL REFERENCES role(id) ON DELETE CASCADE,
                                         PRIMARY KEY (user_id, role_id)
);

-- Seed default roles
INSERT INTO role (code)
VALUES ('ADMIN'), ('FRONT'), ('ORGANIZER'), ('CUSTOMER')
ON CONFLICT (code) DO NOTHING;

-- Customer profile (optional link to user_account)
CREATE TABLE IF NOT EXISTS customer_profile (
                                                id          BIGSERIAL PRIMARY KEY,
                                                user_id     BIGINT UNIQUE REFERENCES user_account(id) ON DELETE SET NULL, -- optional 1:1 link to user_account
                                                first_name  VARCHAR(100) NOT NULL,
                                                last_name   VARCHAR(100) NOT NULL,
                                                email       VARCHAR(200) NOT NULL, -- case-insensitive uniqueness enforced via functional index below
                                                phone       VARCHAR(50),
                                                birthday    DATE,
                                                created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                                updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                                CONSTRAINT chk_customer_name_not_blank CHECK (btrim(first_name) <> '' AND btrim(last_name) <> '')
);

-- Organizer (partner) with one optional login per organizer
CREATE TABLE IF NOT EXISTS organizer (
                                         id          BIGSERIAL PRIMARY KEY,
                                         user_id     BIGINT UNIQUE REFERENCES user_account(id) ON DELETE SET NULL, -- one login per organizer
                                         name        VARCHAR(200) NOT NULL,
                                         email       VARCHAR(200), -- case-insensitive uniqueness can be added later if required
                                         phone       VARCHAR(50),
                                         is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
                                         created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                                         updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Case-insensitive email uniqueness (functional unique indexes)
CREATE UNIQUE INDEX IF NOT EXISTS uq_user_account_email_lower
    ON user_account (lower(email));

CREATE UNIQUE INDEX IF NOT EXISTS uq_customer_profile_email_lower
    ON customer_profile (lower(email));

-- Performance indexes for many-to-many mapping
CREATE INDEX IF NOT EXISTS idx_user_role_user ON user_role(user_id);
CREATE INDEX IF NOT EXISTS idx_user_role_role ON user_role(role_id);

-- Triggers to automatically update the "updated_at" column
-- Uses nature_connect.set_updated_at() function defined in V1
DROP TRIGGER IF EXISTS trg_user_account_updated ON user_account;
CREATE TRIGGER trg_user_account_updated
    BEFORE UPDATE ON user_account
    FOR EACH ROW EXECUTE FUNCTION nature_connect.set_updated_at();

DROP TRIGGER IF EXISTS trg_customer_profile_updated ON customer_profile;
CREATE TRIGGER trg_customer_profile_updated
    BEFORE UPDATE ON customer_profile
    FOR EACH ROW EXECUTE FUNCTION nature_connect.set_updated_at();

DROP TRIGGER IF EXISTS trg_organizer_updated ON organizer;
CREATE TRIGGER trg_organizer_updated
    BEFORE UPDATE ON organizer
    FOR EACH ROW EXECUTE FUNCTION nature_connect.set_updated_at();
