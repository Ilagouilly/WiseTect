CREATE TABLE IF NOT EXISTS user_requirements (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    project_name VARCHAR(255),
    project_description TEXT,
    answers JSONB,
    answers_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_requirements_user_id ON user_requirements(user_id);
