CREATE TABLE IF NOT EXISTS architecture_suggestions (
    id BIGSERIAL PRIMARY KEY,
    requirement_id BIGINT NOT NULL,
    diagram_json TEXT,
    analysis_json TEXT,
    raw_llm_response TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_suggestions_requirement_id ON architecture_suggestions(requirement_id);
