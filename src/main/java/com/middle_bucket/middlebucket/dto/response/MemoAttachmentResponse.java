package com.middle_bucket.middlebucket.dto.response;


import com.middle_bucket.middlebucket.entity.MemoAttachment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MemoAttachmentResponse {
    private Long id;
    private Long memoId;
    private String filename;
    private String originalName;
    private String mimeType;
    private Integer size;
    private Long uploadedById;
    private String uploadedByName;
    private LocalDateTime uploadedAt;

    public static MemoAttachmentResponse from(MemoAttachment attachment) {
        MemoAttachmentResponse dto = new MemoAttachmentResponse();
        dto.setId(attachment.getId());
        dto.setFilename(attachment.getFilename());
        dto.setOriginalName(attachment.getOriginalName());
        dto.setMimeType(attachment.getMimeType());
        dto.setSize(attachment.getSize());
        dto.setUploadedAt(attachment.getUploadedAt());

        if (attachment.getMemo() != null) {
            dto.setMemoId(attachment.getMemo().getId());
        }
        if (attachment.getUploadedBy() != null) {
            dto.setUploadedById(attachment.getUploadedBy().getId());
            dto.setUploadedByName(attachment.getUploadedBy().getName());
        }
        return dto;
    }
}
