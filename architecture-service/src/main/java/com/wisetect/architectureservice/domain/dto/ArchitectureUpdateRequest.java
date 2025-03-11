package com.wisetect.architectureservice.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchitectureUpdateRequest {
    @NotNull
    private Long id;
    private String diagramJson;
    private String analysisJson;
    private String projectName;
    private String requirements;
}
