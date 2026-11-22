CREATE TABLE dim_kpi (
    kpi_id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    formula_version VARCHAR(32) NOT NULL,
    owner VARCHAR(255),
    reporting_grain VARCHAR(128),
    classification VARCHAR(32) NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE
);

CREATE TABLE fact_kpi_observation (
    observation_id BIGSERIAL PRIMARY KEY,
    kpi_id VARCHAR(64) NOT NULL REFERENCES dim_kpi(kpi_id),
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    value NUMERIC(18,4),
    unit VARCHAR(32),
    source_system VARCHAR(128) NOT NULL,
    quality_status VARCHAR(32) NOT NULL,
    loaded_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (kpi_id, period_start, period_end, source_system)
);

CREATE TABLE fact_refresh_run (
    refresh_id BIGSERIAL PRIMARY KEY,
    source_system VARCHAR(128) NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(32) NOT NULL,
    message VARCHAR(1000)
);
