CREATE TABLE tbl_users (
    id VARCHAR(255) NOT NULL,
    username VARCHAR(255),
    password VARCHAR(255),
    full_name VARCHAR(255),
    dob DATE,
    PRIMARY KEY (id)
);

CREATE TABLE tbl_roles (
    id INT NOT NULL,
    name VARCHAR(255),
    description VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE tbl_permissions (
    id INT NOT NULL,
    name VARCHAR(255),
    description VARCHAR(255),
    code VARCHAR(255) NOT NULL UNIQUE,
    api_path VARCHAR(255),
    method VARCHAR(255),
    module VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE tbl_users_roles (
    user_id VARCHAR(255) NOT NULL,
    roles_id INT NOT NULL,
    PRIMARY KEY (user_id, roles_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES tbl_users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (roles_id) REFERENCES tbl_roles(id)
);

CREATE TABLE tbl_roles_permissions (
    role_id INT NOT NULL,
    permissions_id INT NOT NULL,
    PRIMARY KEY (role_id, permissions_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES tbl_roles(id),
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permissions_id) REFERENCES tbl_permissions(id)
);

CREATE TABLE invalidated_token (
    id VARCHAR(255) NOT NULL,
    expiry_time DATETIME2,
    PRIMARY KEY (id)
);
