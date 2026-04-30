package com.middle_bucket.middlebucket.dto.response;

import com.middle_bucket.middlebucket.entity.Task;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private String priority;
    private LocalDate dueDate;
    private Long assigneeId;
    private String assigneeName;
    private Long createdById;
    private String createdByName;
    private String completionNote;
    private LocalDateTime completedAt;
    private String revisionNote;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskResponse from(Task task) {
        TaskResponse dto = new TaskResponse();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus().name());
        dto.setPriority(task.getPriority().name());
        dto.setDueDate(task.getDueDate());
        dto.setCompletionNote(task.getCompletionNote());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setRevisionNote(task.getRevisionNote());
        dto.setReviewedAt(task.getReviewedAt());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        if (task.getAssignee() != null) {
            dto.setAssigneeId(task.getAssignee().getId());
            dto.setAssigneeName(task.getAssignee().getName());
        }
        if (task.getCreatedBy() != null) {
            dto.setCreatedById(task.getCreatedBy().getId());
            dto.setCreatedByName(task.getCreatedBy().getName());
        }
        return dto;
    }
}
