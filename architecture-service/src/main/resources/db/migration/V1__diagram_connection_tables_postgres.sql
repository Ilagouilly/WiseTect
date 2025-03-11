CREATE TABLE IF NOT EXISTS diagram_connection (
    id VARCHAR(255) PRIMARY KEY,
    source_id VARCHAR(255) NOT NULL,
    target_id VARCHAR(255) NOT NULL,
    type VARCHAR(100),
    label VARCHAR(255),
    properties JSONB
);

CREATE INDEX idx_connection_source ON diagram_connection(source_id);
CREATE INDEX idx_connection_target ON diagram_connection(target_id);
