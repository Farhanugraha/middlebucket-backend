package com.middle_bucket.middlebucket.dto.response;

import com.middle_bucket.middlebucket.entity.TaskAttachment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskAttachmentResponse {

    private Long id;
    private Long taskId;
    private String type;
    private String filename;
    private String filePath;
    private String originalName;
    private String mimeType;
    private Integer size;
    private Long uploadedById;
    private String uploadedByName;
    private LocalDateTime uploadedAt;

    public static TaskAttachmentResponse from(TaskAttachment attachment) {
        TaskAttachmentResponse dto = new TaskAttachmentResponse();
        dto.setId(attachment.getId());
        dto.setType(attachment.getType().name());
        dto.setFilename(attachment.getFilename());
        dto.setFilePath("tasks/" + attachment.getFilename()); //
        dto.setOriginalName(attachment.getOriginalName());
        dto.setMimeType(attachment.getMimeType());
        dto.setSize(attachment.getSize());
        dto.setUploadedAt(attachment.getUploadedAt());

        if (attachment.getTask() != null) {
            dto.setTaskId(attachment.getTask().getId());
        }
        if (attachment.getUploadedBy() != null) {
            dto.setUploadedById(attachment.getUploadedBy().getId());
            dto.setUploadedByName(attachment.getUploadedBy().getName());
        }
        return dto;
    }
}