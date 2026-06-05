package com.middle_bucket.middlebucket.dto.response;

import com.middle_bucket.middlebucket.entity.TaskHistory;

import java.time.LocalDateTime;

public record TaskHistoryResponse(
        Long id,
        String actorName,
        String actorEmail,
        String action,
        String oldStatus,
        String newStatus,
        String note,
        LocalDateTime createdAt
) {
    public static TaskHistoryResponse from(TaskHistory h) {
        return new TaskHistoryResponse(
                h.getId(),
                h.getActor().getName(),
                h.getActor().getEmail(),
                h.getAction().name(),
                h.getOldStatus() != null ? h.getOldStatus().name() : null,
                h.getNewStatus() != null ? h.getNewStatus().name() : null,
                h.getNote(),
                h.getCreatedAt()
        );
    }
}
