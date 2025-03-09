CREATE TABLE IF NOT EXISTS diagram_component (
    id VARCHAR(255) PRIMARY KEY,
    type VARCHAR(100),
    name VARCHAR(255),
    description TEXT,
    properties JSON,
    position_x INT,
    position_y INT
);
