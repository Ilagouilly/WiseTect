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
public class ArchitectureAnalysis {
    private String summary;
    private Map<String, Strength> strengths;
    private Map<String, Weakness> weaknesses;
    private Map<String, Recommendation> recommendations;
    private Map<String, Object> metrics;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Strength {
        private String title;
        private String description;
        private String impact;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Weakness {
        private String title;
        private String description;
        private String impact;
        private String mitigationStrategy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Recommendation {
        private String title;
        private String description;
        private String priority;
        private String effort;
        private String impact;
    }
}
