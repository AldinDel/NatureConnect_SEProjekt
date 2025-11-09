CREATE TABLE IF NOT EXISTS booking (
                                       id BIGSERIAL PRIMARY KEY,
                                       first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
    );
