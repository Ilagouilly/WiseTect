CREATE TABLE IF NOT EXISTS user_requirements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    project_name VARCHAR(255),
    project_description TEXT,
    answers JSON,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_requirements_user_id ON user_requirements(user_id);
