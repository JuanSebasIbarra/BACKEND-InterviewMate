-- =============================================================================
-- V1 - BASELINE SCHEMA
-- =============================================================================
-- Este script documenta el esquema que ya existe en Supabase/PostgreSQL
-- tal como fue creado por Hibernate ddl-auto=update antes de adoptar Flyway.
--
-- IMPORTANTE: No se ejecuta en bases de datos existentes porque usamos
-- spring.flyway.baseline-on-migrate=true con baseline-version=1.
-- Flyway marca V1 como "aplicado" sin ejecutarlo y continúa desde V2.
--
-- Para instalaciones nuevas (desde cero), este script sí se ejecuta.
-- =============================================================================

-- ------------------------------------
-- TABLA: users
-- ------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id               BIGSERIAL PRIMARY KEY,
    username         VARCHAR(255)   NOT NULL UNIQUE,
    email            VARCHAR(255)   NOT NULL UNIQUE,
    password         VARCHAR(255)   NOT NULL,
    perfil_profesional VARCHAR(5000),
    created_at       TIMESTAMPTZ
);

-- ------------------------------------
-- TABLA: user_roles  (colección @ElementCollection)
-- ------------------------------------
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id),
    role    VARCHAR(255)
);

-- ------------------------------------
-- TABLA: interview_template
-- ------------------------------------
CREATE TABLE IF NOT EXISTS interview_template (
    id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          BIGINT        NOT NULL REFERENCES users(id),
    enterprise       VARCHAR(255)  NOT NULL,
    type             VARCHAR(255)  NOT NULL,
    position         VARCHAR(255)  NOT NULL,
    working_area     VARCHAR(255),
    description      VARCHAR(255),
    requirements     TEXT,
    goals            TEXT,
    business_context TEXT,
    status           VARCHAR(255)  DEFAULT 'DRAFT',
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP
);

-- ------------------------------------
-- TABLA: interview_session
-- ------------------------------------
CREATE TABLE IF NOT EXISTS interview_session (
    id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    template_id    UUID         NOT NULL REFERENCES interview_template(id),
    attempt_number INTEGER,
    status         VARCHAR(255) DEFAULT 'PENDING',
    started_at     TIMESTAMP,
    completed_at   TIMESTAMP
);

-- ------------------------------------
-- TABLA: interview_question
-- ------------------------------------
CREATE TABLE IF NOT EXISTS interview_question (
    id           UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id   UUID    NOT NULL REFERENCES interview_session(id),
    order_index  INTEGER,
    question     TEXT    NOT NULL,
    answer       TEXT,
    ai_feedback  TEXT,
    score        DOUBLE PRECISION,
    ai_model     VARCHAR(255),
    created_at   TIMESTAMP,
    answered_at  TIMESTAMP
);

-- ------------------------------------
-- TABLA: interview_result
-- ------------------------------------
CREATE TABLE IF NOT EXISTS interview_result (
    id                 UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id         UUID    NOT NULL UNIQUE REFERENCES interview_session(id),
    general_feedback   TEXT,
    strengths          TEXT,
    weaknesses         TEXT,
    total_score        DOUBLE PRECISION,
    status             VARCHAR(255) DEFAULT 'PENDING_REVIEW',
    ai_model           VARCHAR(255),
    total_tokens_used  INTEGER,
    generated_at       TIMESTAMP
);

-- ------------------------------------
-- TABLA: study_session
-- ------------------------------------
CREATE TABLE IF NOT EXISTS study_session (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    BIGINT       NOT NULL REFERENCES users(id),
    topic      VARCHAR(255) NOT NULL,
    created_at TIMESTAMP
);

-- ------------------------------------
-- TABLA: study_question
-- ------------------------------------
CREATE TABLE IF NOT EXISTS study_question (
    id               UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    study_session_id UUID    NOT NULL REFERENCES study_session(id),
    order_index      INTEGER,
    question_text    TEXT    NOT NULL,
    difficulty       VARCHAR(255),
    type             VARCHAR(255)
);

