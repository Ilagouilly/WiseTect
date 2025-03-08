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
public class DiagramComponent {
    private String id;
    private String type; // e.g., "service", "database", "queue", etc.
    private String name;
    private String description;
    private Map<String, Object> properties;
    private Position position;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Position {
        private int x;
        private int y;
    }
}
