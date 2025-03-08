package com.wisetect.architectureservice.domain.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagramConnection {
    private String id;
    private String sourceId;
    private String targetId;
    private String type; // e.g., "sync", "async", "event", etc.
    private String label;
    private Map<String, Object> properties;
}
