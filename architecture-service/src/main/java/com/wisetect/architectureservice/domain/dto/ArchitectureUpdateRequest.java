package com.wisetect.architectureservice.domain.dto;

import com.wisetect.architectureservice.domain.model.ArchitectureDiagram;

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
    @NotNull(message = "Requirement ID is required")
    private Long requirementId;

    @NotNull(message = "Updated diagram is required")
    private ArchitectureDiagram updatedDiagram;

    private String userFeedback;
}
