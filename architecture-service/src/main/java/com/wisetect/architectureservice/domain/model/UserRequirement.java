package com.wisetect.architectureservice.domain.model;

import java.time.LocalDateTime;
import java.util.Map;

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
@Table("user_requirements")
public class UserRequirement {
    @Id
    private Long id;
    private String userId;
    private String projectName;
    private String projectDescription;
    private Map<String, Object> answers; // Stores question ID -> answer
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
