package com.wisetect.architectureservice.domain.dto;

import com.wisetect.architectureservice.domain.model.ArchitectureAnalysis;
import com.wisetect.architectureservice.domain.model.ArchitectureDiagram;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchitectureSuggestionResponse {
    private Long id;
    private Long requirementId;
    private ArchitectureDiagram diagram;
    private ArchitectureAnalysis analysis;
    private Integer version;
}
