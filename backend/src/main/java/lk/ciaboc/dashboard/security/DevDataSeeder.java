package lk.ciaboc.dashboard.security;

import lk.ciaboc.dashboard.auth.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Seeds the in-memory H2 database with the RBAC schema and sample data
 * when the "dev" profile is active.
 */
@Profile("dev")
@Configuration
public class DevDataSeeder {
    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

    @Bean
    CommandLineRunner seedDevData(JdbcTemplate jdbc, AuthService authService) {
        return args -> {
            log.info("🌱 Seeding dev data...");

            // --- Schema (mirrors Flyway V1 + V2 but simplified for H2) ---
            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS dim_kpi (
                    kpi_id VARCHAR(64) PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    formula_version VARCHAR(32) NOT NULL,
                    owner VARCHAR(255),
                    reporting_grain VARCHAR(128),
                    classification VARCHAR(32) NOT NULL,
                    effective_from DATE NOT NULL,
                    effective_to DATE
                )
            """);

            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS fact_kpi_observation (
                    observation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    kpi_id VARCHAR(64) NOT NULL,
                    period_start DATE NOT NULL,
                    period_end DATE NOT NULL,
                    value NUMERIC(18,4),
                    unit VARCHAR(32),
                    source_system VARCHAR(128) NOT NULL,
                    quality_status VARCHAR(32) NOT NULL,
                    loaded_at TIMESTAMP WITH TIME ZONE NOT NULL,
                    UNIQUE (kpi_id, period_start, period_end, source_system)
                )
            """);

            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS fact_refresh_run (
                    refresh_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    source_system VARCHAR(128) NOT NULL,
                    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
                    completed_at TIMESTAMP WITH TIME ZONE,
                    status VARCHAR(32) NOT NULL,
                    message VARCHAR(1000)
                )
            """);

            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS organizational_unit (
                    organizational_unit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    code VARCHAR(64) NOT NULL UNIQUE,
                    name VARCHAR(255) NOT NULL,
                    active BOOLEAN NOT NULL DEFAULT TRUE,
                    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
            """);

            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS dashboard_user (
                    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    external_subject VARCHAR(255) NOT NULL UNIQUE,
                    email VARCHAR(255) NOT NULL UNIQUE,
                    display_name VARCHAR(255) NOT NULL,
                    organizational_unit_id BIGINT,
                    active BOOLEAN NOT NULL DEFAULT TRUE,
                    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
            """);

            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS dashboard_role (
                    role_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    code VARCHAR(64) NOT NULL UNIQUE,
                    name VARCHAR(255) NOT NULL,
                    active BOOLEAN NOT NULL DEFAULT TRUE
                )
            """);

            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS dashboard_permission (
                    permission_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    code VARCHAR(128) NOT NULL UNIQUE,
                    module VARCHAR(64) NOT NULL,
                    action VARCHAR(32) NOT NULL,
                    sensitive BOOLEAN NOT NULL DEFAULT FALSE
                )
            """);

            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS user_role (
                    user_role_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    user_id BIGINT NOT NULL,
                    role_id BIGINT NOT NULL,
                    organizational_unit_id BIGINT,
                    active BOOLEAN NOT NULL DEFAULT TRUE,
                    assigned_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    revoked_at TIMESTAMP WITH TIME ZONE,
                    UNIQUE (user_id, role_id)
                )
            """);

            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS role_permission (
                    role_id BIGINT NOT NULL,
                    permission_id BIGINT NOT NULL,
                    PRIMARY KEY (role_id, permission_id)
                )
            """);

            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS role_pin (
                    role_pin_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    user_role_id BIGINT NOT NULL UNIQUE,
                    pin_hash VARCHAR(255) NOT NULL,
                    status VARCHAR(32) NOT NULL,
                    failed_attempts INTEGER NOT NULL DEFAULT 0,
                    locked_until TIMESTAMP WITH TIME ZONE,
                    expires_at TIMESTAMP WITH TIME ZONE,
                    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    revoked_at TIMESTAMP WITH TIME ZONE
                )
            """);

            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS audit_log (
                    audit_log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    actor_subject VARCHAR(255),
                    action VARCHAR(128) NOT NULL,
                    outcome VARCHAR(32) NOT NULL,
                    target_type VARCHAR(64),
                    target_id VARCHAR(255),
                    detail VARCHAR(2000)
                )
            """);

            // --- Seed roles ---
            jdbc.execute("""
                INSERT INTO dashboard_role(code, name) VALUES
                    ('COMMISSION', 'Commission'),
                    ('DIRECTOR_GENERAL', 'Director General'),
                    ('ADDITIONAL_DG', 'Additional Director General'),
                    ('DIRECTOR', 'Director'),
                    ('MANAGER', 'Manager'),
                    ('DATA_STEWARD', 'Data Steward'),
                    ('ICT_ADMIN', 'ICT Administrator')
            """);

            // --- Seed organizational unit ---
            jdbc.execute("""
                INSERT INTO organizational_unit(code, name) VALUES
                    ('HQ', 'Head Office'),
                    ('INVEST', 'Investigation Division'),
                    ('LEGAL', 'Legal Division')
            """);

            // --- Seed dev user ---
            jdbc.execute("""
                INSERT INTO dashboard_user(external_subject, email, display_name, organizational_unit_id)
                VALUES ('dev-user-001', 'dev@ciaboc.lk', 'Dev User', 1)
            """);

            // --- Assign roles: Director General + ICT Admin ---
            jdbc.execute("""
                INSERT INTO user_role(user_id, role_id, organizational_unit_id) VALUES
                    (1, 2, 1),
                    (1, 7, 1)
            """);

            // --- Provision PINs (PIN: 12345678) ---
            var assignments = new long[]{1L, 2L};
            for (long assignmentId : assignments) {
                authService.findAssignment(assignmentId)
                        .ifPresent(authService::provisionPin);
            }

            log.info("✅ Dev data seeded. Dev user: dev@ciaboc.lk, subject: dev-user-001");
            log.info("💡 SSO bypass active at /oauth2/authorization/ciaboc");
            log.info("💡 After SSO, select a role and use any 8-digit PIN shown in the logs above.");
        };
    }
}
