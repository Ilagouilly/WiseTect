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
    private String type;
    private List<Component> components;
    private List<Connection> connections;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Component {
        private String id;
        private String name;
        private String type;
        private String description;
        private Map<String, Object> properties;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Connection {
        private String id;
        private String source;
        private String target;
        private String type;
        private String description;
        private Map<String, Object> properties;
    }
}
