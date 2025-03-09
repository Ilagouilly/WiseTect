CREATE TABLE IF NOT EXISTS requirement_questions (
    id BIGSERIAL PRIMARY KEY,
    question_text TEXT,
    question_type VARCHAR(50),
    options TEXT[],
    display_order INTEGER,
    category VARCHAR(100)
);

CREATE INDEX idx_question_category ON requirement_questions(category);
CREATE INDEX idx_question_display_order ON requirement_questions(display_order);
