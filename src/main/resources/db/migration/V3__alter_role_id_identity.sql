ALTER TABLE tbl_users_roles DROP CONSTRAINT fk_user_roles_role;
ALTER TABLE tbl_roles_permissions DROP CONSTRAINT fk_role_permissions_role;

CREATE TABLE tbl_roles_new (
    id INT IDENTITY(1,1) NOT NULL,
    name VARCHAR(255),
    description VARCHAR(255),
    PRIMARY KEY (id)
);

IF EXISTS (SELECT 1 FROM tbl_roles)
BEGIN
    SET IDENTITY_INSERT tbl_roles_new ON;
    INSERT INTO tbl_roles_new (id, name, description) SELECT id, name, description FROM tbl_roles;
    SET IDENTITY_INSERT tbl_roles_new OFF;
END

DROP TABLE tbl_roles;
EXEC sp_rename 'tbl_roles_new', 'tbl_roles';

ALTER TABLE tbl_users_roles ADD CONSTRAINT fk_user_roles_role FOREIGN KEY (roles_id) REFERENCES tbl_roles(id);
ALTER TABLE tbl_roles_permissions ADD CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES tbl_roles(id);
