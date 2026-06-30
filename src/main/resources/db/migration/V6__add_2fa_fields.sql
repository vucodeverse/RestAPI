ALTER TABLE tbl_users ADD is_2fa_enabled BIT DEFAULT 0 NOT NULL;
ALTER TABLE tbl_users ADD totp_secret VARCHAR(255);
