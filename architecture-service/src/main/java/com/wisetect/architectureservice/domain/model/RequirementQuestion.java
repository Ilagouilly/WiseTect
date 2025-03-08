package com.wisetect.architectureservice.domain.model;

import java.util.List;

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
@Table("requirement_questions")
public class RequirementQuestion {
    @Id
    private Long id;
    private String questionText;
    private String questionType; // SINGLE_CHOICE, MULTIPLE_CHOICE, TEXT, SLIDER
    private List<String> options;
    private Integer displayOrder;
    private String category; // e.g., "general", "scalability", "security", etc.
}
