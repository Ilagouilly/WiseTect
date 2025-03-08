package com.wisetect.architectureservice.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("architecture_suggestions")
public class ArchitectureSuggestion {
    @Id
    private Long id;
    private Long requirementId;
    private String diagramJson;
    private String analysisJson;
    private String rawLlmResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;
    private Boolean isActive;
}
