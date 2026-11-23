CREATE TABLE organizational_unit (
    organizational_unit_id BIGSERIAL PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dashboard_user (
    user_id BIGSERIAL PRIMARY KEY,
    external_subject VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    organizational_unit_id BIGINT REFERENCES organizational_unit(organizational_unit_id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dashboard_role (
    role_id BIGSERIAL PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE dashboard_permission (
    permission_id BIGSERIAL PRIMARY KEY,
    code VARCHAR(128) NOT NULL UNIQUE,
    module VARCHAR(64) NOT NULL,
    action VARCHAR(32) NOT NULL,
    sensitive BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE user_role (
    user_role_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES dashboard_user(user_id),
    role_id BIGINT NOT NULL REFERENCES dashboard_role(role_id),
    organizational_unit_id BIGINT REFERENCES organizational_unit(organizational_unit_id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    assigned_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP WITH TIME ZONE,
    UNIQUE (user_id, role_id)
);

CREATE TABLE role_permission (
    role_id BIGINT NOT NULL REFERENCES dashboard_role(role_id),
    permission_id BIGINT NOT NULL REFERENCES dashboard_permission(permission_id),
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE role_pin (
    role_pin_id BIGSERIAL PRIMARY KEY,
    user_role_id BIGINT NOT NULL UNIQUE REFERENCES user_role(user_role_id),
    pin_hash VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    failed_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE audit_log (
    audit_log_id BIGSERIAL PRIMARY KEY,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actor_subject VARCHAR(255),
    action VARCHAR(128) NOT NULL,
    outcome VARCHAR(32) NOT NULL,
    target_type VARCHAR(64),
    target_id VARCHAR(255),
    detail VARCHAR(2000)
);

CREATE INDEX idx_dashboard_user_external_subject ON dashboard_user(external_subject);
CREATE INDEX idx_user_role_user ON user_role(user_id);
CREATE INDEX idx_user_role_role ON user_role(role_id);
CREATE INDEX idx_role_pin_status ON role_pin(status);
CREATE INDEX idx_audit_log_occurred_at ON audit_log(occurred_at);

INSERT INTO dashboard_role(code, name) VALUES
    ('COMMISSION', 'Commission'),
    ('DIRECTOR_GENERAL', 'Director General'),
    ('ADDITIONAL_DG', 'Additional Director General'),
    ('DIRECTOR', 'Director'),
    ('MANAGER', 'Manager'),
    ('DATA_STEWARD', 'Data Steward'),
    ('ICT_ADMIN', 'ICT Administrator');
