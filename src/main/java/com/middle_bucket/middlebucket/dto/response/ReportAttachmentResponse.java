package com.middle_bucket.middlebucket.dto.response;

import com.middle_bucket.middlebucket.entity.ReportAttachment;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReportAttachmentResponse {

    private Long id;
    private Long reportId;
    private String filename;
    private String originalName;
    private String mimeType;
    private Integer size;
    private Long uploadedById;
    private String uploadedByName;
    private LocalDateTime uploadedAt;

    public static ReportAttachmentResponse from(ReportAttachment attachment) {
        ReportAttachmentResponse dto = new ReportAttachmentResponse();
        dto.setId(attachment.getId());
        dto.setFilename(attachment.getFilename());
        dto.setOriginalName(attachment.getOriginalName());
        dto.setMimeType(attachment.getMimeType());
        dto.setSize(attachment.getSize());
        dto.setUploadedAt(attachment.getUploadedAt());

        if (attachment.getReport() != null) {
            dto.setReportId(attachment.getReport().getId());
        }
        if (attachment.getUploadedBy() != null) {
            dto.setUploadedById(attachment.getUploadedBy().getId());
            dto.setUploadedByName(attachment.getUploadedBy().getName());
        }
        return dto;
    }

}
