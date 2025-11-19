SET search_path TO nature_connect;

ALTER TABLE IF EXISTS booking_equipment DROP CONSTRAINT IF EXISTS booking_equipment_booking_id_fkey;
ALTER TABLE IF EXISTS booking_participant DROP CONSTRAINT IF EXISTS booking_participant_booking_id_fkey;
ALTER TABLE IF EXISTS payment_transaction DROP CONSTRAINT IF EXISTS payment_transaction_booking_id_fkey;


CREATE TABLE IF NOT EXISTS voucher (
    code                    VARCHAR(50) PRIMARY KEY,
    discount_percent        INT NOT NULL CHECK (discount_percent BETWEEN 0 AND 100),
    valid_from              DATE,
    valid_until             DATE,
    max_usage               INT DEFAULT NULL,
    used_count              INT NOT NULL DEFAULT 0,

    CONSTRAINT chk_voucher_dates
        CHECK (valid_from IS NULL OR valid_until IS NULL OR valid_from < valid_until),
    CONSTRAINT chk_voucher_usage
        CHECK (used_count >= 0 AND (max_usage IS NULL OR used_count <= max_usage))
    );

CREATE INDEX IF NOT EXISTS idx_voucher_validity ON voucher(valid_from, valid_until);

ALTER TABLE booking
    DROP COLUMN IF EXISTS customer_id,
    DROP COLUMN IF EXISTS is_guest,
    DROP COLUMN IF EXISTS first_name,
    DROP COLUMN IF EXISTS last_name,
    DROP COLUMN IF EXISTS email,
    DROP COLUMN IF EXISTS status,
    DROP COLUMN IF EXISTS seats,
    DROP COLUMN IF EXISTS payment_method,
    DROP COLUMN IF EXISTS payment_status,
    DROP COLUMN IF EXISTS voucher_code,
    DROP COLUMN IF EXISTS voucher_value,
    DROP COLUMN IF EXISTS unit_price,
    DROP COLUMN IF EXISTS total_price,
    DROP COLUMN IF EXISTS confirmed_at,
    DROP COLUMN IF EXISTS cancelled_at,
    DROP COLUMN IF EXISTS updated_at;

ALTER TABLE booking

    ADD COLUMN event_id BIGINT      REFERENCES event(id) ON DELETE RESTRICT,
    ADD COLUMN booker_first_name    VARCHAR(100),
    ADD COLUMN booker_last_name     VARCHAR(100),
    ADD COLUMN booker_email         VARCHAR(200),
    ADD COLUMN audience             VARCHAR(30),
    ADD COLUMN seats                INT NOT NULL DEFAULT 1,
    ADD COLUMN status               VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    ADD COLUMN payment_status       VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    ADD COLUMN payment_method       VARCHAR(30),
    ADD COLUMN voucher_code         VARCHAR(50) REFERENCES voucher(code),
    ADD COLUMN discount_amount      NUMERIC(10,2) DEFAULT 0,
    ADD COLUMN total_price          NUMERIC(10,2) NOT NULL DEFAULT 0,
    ADD COLUMN special_notes        TEXT,
    ADD COLUMN created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW();

ALTER TABLE booking
    ADD CONSTRAINT check_booker_email
        CHECK (booker_email ~* '^[^@]+@[^@]+\.[^@]+'),
    ADD CONSTRAINT check_booking_status
        CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED')),
    ADD CONSTRAINT check_payment_status
        CHECK ( payment_status IN ('UNPAID', 'PAID', 'PENDING', 'FAILED')),
    ADD CONSTRAINT check_payment_method
        CHECK (payment_method IS NULL OR payment_method IN ('CREDIT_CARD', 'PAYPAL', 'INVOICE', 'ON_SITE')),
    ADD CONSTRAINT check_audience
        CHECK (audience IN ('INDIVIDUAL', 'GROUP', 'COMPANY')),
    ADD CONSTRAINT check_seats
        CHECK (seats >= 1),
    ADD CONSTRAINT check_total_price
        CHECK (total_price >= 0),
    ADD CONSTRAINT check_discount_amount
        CHECK (discount_amount >= 0);

CREATE INDEX IF NOT EXISTS idx_booking_event_id ON booking(event_id);
CREATE INDEX IF NOT EXISTS idx_booking_status  ON booking(status);
CREATE INDEX IF NOT EXISTS idx_booking_voucher ON booking(voucher_code);

CREATE TABLE IF NOT EXISTS booking_participant (
    id BIGSERIAL        PRIMARY KEY,
    booking_id          BIGINT NOT NULL REFERENCES booking(id) ON DELETE CASCADE,
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    age                 INT NOT NULL CHECK (age > 0)
);

CREATE INDEX IF NOT EXISTS idx_participant_booking ON booking_participant(booking_id);

CREATE TABLE IF NOT EXISTS booking_equipment (
    id BIGSERIAL        PRIMARY KEY,
    booking_id          BIGINT NOT NULL REFERENCES booking(id) ON DELETE CASCADE,
    equipment_id        BIGINT NOT NULL REFERENCES equipment(id) ON DELETE RESTRICT,
    quantity            INT NOT NULL CHECK (quantity >= 1),
    unit_price          NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (unit_price >= 0),
    total_price         NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (total_price >= 0),

    CONSTRAINT unique_booking_equipment UNIQUE (booking_id, equipment_id)
    );

CREATE INDEX IF NOT EXISTS idx_booking_equipment_booking ON booking_equipment(booking_id);

CREATE TABLE IF NOT EXISTS payment_transaction (
    id BIGSERIAL        PRIMARY KEY,
    booking_id          BIGINT NOT NULL REFERENCES booking(id) ON DELETE CASCADE,
    payment_method      VARCHAR(30) NOT NULL,
    amount              NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_payment_transaction_status
        CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED')),
    CONSTRAINT chk_payment_transaction_method
        CHECK (payment_method IN ('CREDIT_CARD', 'PAYPAL', 'INVOICE', 'ON_SITE'))
    );

CREATE INDEX IF NOT EXISTS idx_payment_booking ON payment_transaction(booking_id);

CREATE UNIQUE INDEX IF NOT EXISTS idx_booking_successful_payment ON payment_transaction(booking_id)
    WHERE status = 'SUCCESS';
