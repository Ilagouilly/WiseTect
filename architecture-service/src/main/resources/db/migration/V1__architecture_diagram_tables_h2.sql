CREATE TABLE IF NOT EXISTS architecture_diagram (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    type VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS diagram_components (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    diagram_id UUID NOT NULL,
    component_id VARCHAR(255),
    name VARCHAR(255),
    type VARCHAR(100),
    description TEXT,
    properties JSON,
    FOREIGN KEY (diagram_id) REFERENCES architecture_diagram(id)
);

CREATE TABLE IF NOT EXISTS diagram_connections (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    diagram_id UUID NOT NULL,
    connection_id VARCHAR(255),
    source VARCHAR(255),
    target VARCHAR(255),
    type VARCHAR(100),
    description TEXT,
    properties JSON,
    FOREIGN KEY (diagram_id) REFERENCES architecture_diagram(id)
);

CREATE INDEX idx_components_diagram_id ON diagram_components(diagram_id);
CREATE INDEX idx_connections_diagram_id ON diagram_connections(diagram_id);
