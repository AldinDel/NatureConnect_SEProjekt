CREATE TABLE IF NOT EXISTS nature_connect.invoice (
                                                      id BIGSERIAL PRIMARY KEY,
                                                      booking_id BIGINT NOT NULL,
                                                      status VARCHAR(50) NOT NULL,
    total NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL
    );
