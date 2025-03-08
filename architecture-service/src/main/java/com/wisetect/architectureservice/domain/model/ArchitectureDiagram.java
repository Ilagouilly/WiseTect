package com.wisetect.architectureservice.domain.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchitectureDiagram {
    private List<DiagramComponent> components;
    private List<DiagramConnection> connections;
    private Map<String, Object> metadata;
}
