package com.middle_bucket.middlebucket.dto.response;


import com.middle_bucket.middlebucket.entity.DailyReport;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DailyReportResponse {
    private Long id;
    private Long userId;
    private String userName;
    private LocalDate reportDate;
    private String content;
    private LocalDateTime createdAt;
    private List<ReportAttachmentResponse> attachments;

    public static DailyReportResponse from(DailyReport report) {
        DailyReportResponse dto = new DailyReportResponse();
        dto.setId(report.getId());
        dto.setReportDate(report.getReportDate());
        dto.setContent(report.getContent());
        dto.setCreatedAt(report.getCreatedAt());

        if (report.getUser() != null) {
            dto.setUserId(report.getUser().getId());
            dto.setUserName(report.getUser().getName());
        }

        // Map attachments
        if (report.getAttachments() != null) {
            dto.setAttachments(report.getAttachments()
                    .stream()
                    .map(ReportAttachmentResponse::from)
                    .toList());
        }
        return dto;
    }

}
