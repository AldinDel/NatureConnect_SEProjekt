SET search_path TO nature_connect;

CREATE TABLE booking (
                         id                BIGSERIAL PRIMARY KEY,

    -- Relations
                         event_id          BIGINT NOT NULL
                             REFERENCES nature_connect.event(id) ON DELETE RESTRICT,
                         customer_id       BIGINT
                                                  REFERENCES nature_connect.customer_profile(id) ON DELETE SET NULL,
                         is_guest          BOOLEAN NOT NULL DEFAULT TRUE,

    -- Contact data (for the booking contact person)
                         first_name        VARCHAR(100) NOT NULL,
                         last_name         VARCHAR(100) NOT NULL,
                         email             VARCHAR(200) NOT NULL,

    -- Booking details
                         seats             INTEGER      NOT NULL DEFAULT 1,
                         status            VARCHAR(20)  NOT NULL DEFAULT 'CONFIRMED',
                         payment_method    VARCHAR(50)  NOT NULL,
                         voucher_code      VARCHAR(50),
                         voucher_value     NUMERIC(10,2) DEFAULT 0,

    -- Pricing
                         unit_price        NUMERIC(10,2) NOT NULL DEFAULT 0,
                         total_price       NUMERIC(10,2) NOT NULL DEFAULT 0,

    -- Timeline
                         confirmed_at      TIMESTAMPTZ,
                         cancelled_at      TIMESTAMPTZ,

    -- Audit
                         created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                         updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Constraints
                         CONSTRAINT chk_booking_seats
                             CHECK (seats >= 1),
                         CONSTRAINT chk_booking_prices
                             CHECK (unit_price >= 0 AND total_price >= 0),
                         CONSTRAINT chk_booking_status
                             CHECK (status IN ('CONFIRMED','CANCELLED')),
                         CONSTRAINT chk_payment_method
                             CHECK (payment_method IN ('INVOICE', 'ON_SITE', 'CREDIT_CARD'))
);

--  Indexes
CREATE UNIQUE INDEX IF NOT EXISTS uq_booking_event_email_lower
    ON booking (event_id, lower(email));

CREATE INDEX IF NOT EXISTS idx_booking_event
    ON booking (event_id);

CREATE INDEX IF NOT EXISTS idx_booking_customer
    ON booking (customer_id);

CREATE INDEX IF NOT EXISTS idx_booking_status
    ON booking (status);

--  Auto-update updated_at
DROP TRIGGER IF EXISTS trg_booking_updated ON booking;

CREATE TRIGGER trg_booking_updated
    BEFORE UPDATE ON booking
    FOR EACH ROW
    EXECUTE FUNCTION nature_connect.set_updated_at();

--  Attendees (additional participants per booking)
CREATE TABLE booking_attendee (
                                  id          BIGSERIAL PRIMARY KEY,
                                  booking_id  BIGINT NOT NULL
                                      REFERENCES booking(id) ON DELETE CASCADE,
                                  first_name  VARCHAR(100) NOT NULL,
                                  last_name   VARCHAR(100) NOT NULL,
                                  birthday    DATE NOT NULL
);
