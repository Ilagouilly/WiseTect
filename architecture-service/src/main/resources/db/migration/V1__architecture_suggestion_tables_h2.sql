CREATE TABLE IF NOT EXISTS architecture_suggestions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    requirement_id BIGINT NOT NULL,
    diagram_json TEXT,
    analysis_json TEXT,
    raw_llm_response TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version INT DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_suggestions_requirement_id ON architecture_suggestions(requirement_id);
