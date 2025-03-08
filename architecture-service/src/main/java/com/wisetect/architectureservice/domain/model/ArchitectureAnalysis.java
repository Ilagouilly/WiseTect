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
public class ArchitectureAnalysis {
    private String summary;
    private List<Tradeoff> tradeoffs;
    private List<String> strengths;
    private List<String> weaknesses;
    private Map<String, Object> metrics;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tradeoff {
        private String aspect;
        private String pros;
        private String cons;
        private String recommendation;
    }
}
