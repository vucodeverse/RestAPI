-- Add email column to users table
ALTER TABLE tbl_users ADD email VARCHAR(255);
-- To avoid failure if email already exists, it's safe to just ADD it.
-- But since it's a new system, we can create a unique constraint.
-- If there are existing records, setting a unique constraint might fail if they all have NULL emails.
-- We will update existing users with dummy emails first to be safe.
UPDATE tbl_users SET email = username + '@localhost' WHERE email IS NULL;
ALTER TABLE tbl_users ALTER COLUMN email VARCHAR(255) NOT NULL;
ALTER TABLE tbl_users ADD CONSTRAINT UC_User_Email UNIQUE (email);

-- Create password reset token table
CREATE TABLE tbl_password_reset_tokens (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    token_hash VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    expiry_date DATETIME2 NOT NULL,
    is_used BIT NOT NULL DEFAULT 0,
    created_at DATETIME2,
    updated_at DATETIME2,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT FK_PasswordResetToken_User FOREIGN KEY (user_id) REFERENCES tbl_users(id) ON DELETE CASCADE
);

CREATE INDEX IDX_PasswordResetToken_UserId ON tbl_password_reset_tokens(user_id);
