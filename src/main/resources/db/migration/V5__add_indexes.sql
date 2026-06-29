CREATE INDEX idx_user_username ON tbl_users(username);
CREATE INDEX idx_permission_code ON tbl_permissions(code);
CREATE INDEX idx_session_user_revoked ON tbl_user_sessions(user_id, is_revoked);
