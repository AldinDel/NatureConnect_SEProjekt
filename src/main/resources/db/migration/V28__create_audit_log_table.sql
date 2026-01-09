-- Create audit_log table for tracking user actions
CREATE TABLE IF NOT EXISTS nature_connect.audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(255),
    user_email VARCHAR(255),
    user_role VARCHAR(50),
    action VARCHAR(500) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    details VARCHAR(1000),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),

    -- Index for faster queries
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES nature_connect.user_account(id) ON DELETE SET NULL
);

-- Create indexes for common queries
CREATE INDEX idx_audit_log_user_id ON nature_connect.audit_log(user_id);
CREATE INDEX idx_audit_log_action_type ON nature_connect.audit_log(action_type);
CREATE INDEX idx_audit_log_entity_type ON nature_connect.audit_log(entity_type);
CREATE INDEX idx_audit_log_timestamp ON nature_connect.audit_log(timestamp DESC);
CREATE INDEX idx_audit_log_entity_lookup ON nature_connect.audit_log(entity_type, entity_id);
