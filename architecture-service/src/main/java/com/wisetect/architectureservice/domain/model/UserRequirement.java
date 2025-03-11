package com.wisetect.architectureservice.domain.model;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_requirements")
@Slf4j
public class UserRequirement {
    @Id
    private Long id;
    private String userId;
    private String projectName;
    private String projectDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Transient
    private Map<String, Object> answers;

    @Column("answers_json")
    private String answersJson;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> getAnswers() {
        if (answers == null && answersJson != null) {
            try {
                answers = objectMapper.readValue(answersJson, new TypeReference<Map<String, Object>>() {
                });
            } catch (JsonProcessingException e) {
                log.error("Error deserializing answers JSON", e);
                answers = Map.of();
            }
        }
        return answers;
    }

    public void setAnswers(Map<String, Object> answers) {
        this.answers = answers;
        try {
            this.answersJson = objectMapper.writeValueAsString(answers);
        } catch (JsonProcessingException e) {
            log.error("Error serializing answers to JSON", e);
            this.answersJson = "{}";
        }
    }
}
