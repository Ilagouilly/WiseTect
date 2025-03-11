CREATE TABLE IF NOT EXISTS architecture_analysis (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    summary TEXT,
    metrics JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS strengths (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    analysis_id UUID NOT NULL,
    title VARCHAR(255),
    description TEXT,
    impact TEXT,
    FOREIGN KEY (analysis_id) REFERENCES architecture_analysis(id)
);

CREATE TABLE IF NOT EXISTS weaknesses (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    analysis_id UUID NOT NULL,
    title VARCHAR(255),
    description TEXT,
    impact TEXT,
    mitigation_strategy TEXT,
    FOREIGN KEY (analysis_id) REFERENCES architecture_analysis(id)
);

CREATE TABLE IF NOT EXISTS recommendations (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    analysis_id UUID NOT NULL,
    title VARCHAR(255),
    description TEXT,
    priority VARCHAR(50),
    effort VARCHAR(50),
    impact TEXT,
    FOREIGN KEY (analysis_id) REFERENCES architecture_analysis(id)
);

CREATE INDEX idx_strengths_analysis_id ON strengths(analysis_id);
CREATE INDEX idx_weaknesses_analysis_id ON weaknesses(analysis_id);
CREATE INDEX idx_recommendations_analysis_id ON recommendations(analysis_id);
