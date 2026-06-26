CREATE TABLE tbl_user_sessions (
    id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    device_info VARCHAR(255),
    ip_address VARCHAR(100),
    refresh_token_id VARCHAR(255),
    is_revoked BIT DEFAULT 0,
    created_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
    last_active DATETIME2 DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_sessions_user FOREIGN KEY (user_id) REFERENCES tbl_users(id)
);
