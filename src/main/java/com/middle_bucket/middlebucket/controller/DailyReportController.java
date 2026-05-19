package com.middle_bucket.middlebucket.controller;

import com.middle_bucket.middlebucket.dto.request.DailyReportRequest;
import com.middle_bucket.middlebucket.dto.response.ApiResponse;
import com.middle_bucket.middlebucket.dto.response.DailyReportResponse;
import com.middle_bucket.middlebucket.dto.response.ReportAttachmentResponse;
import com.middle_bucket.middlebucket.service.DailyReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/reports")
public class DailyReportController {

    private final DailyReportService dailyReportService;

    public DailyReportController(DailyReportService dailyReportService) {
        this.dailyReportService = dailyReportService;
    }

    // Get all reports — all role
    @GetMapping
    public ResponseEntity<ApiResponse<List<DailyReportResponse>>> getAllReports() {
        return ResponseEntity.ok(ApiResponse.succes(
                "Berhasil mengambil reports",
                dailyReportService.getAllReports()));
    }


    // Get report by id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DailyReportResponse>> getReportById(
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.succes(
                    "Berhasil",
                    dailyReportService.getReportById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Get my reports
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<DailyReportResponse>>> getMyReports(
            Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.succes(
                "Berhasil mengambil my reports",
                dailyReportService.getReportsByUser(authentication.getName())));
    }

    // Create report — all role
    @PostMapping
    public ResponseEntity<ApiResponse<DailyReportResponse>> createReport(
            @RequestBody DailyReportRequest request,
            Authentication authentication) {
        try {
            DailyReportResponse report = dailyReportService.createReport(
                    request, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.succes("Report berhasil dibuat", report));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Upload attachment
    @PostMapping("/{id}/attachments")
    public ResponseEntity<ApiResponse<ReportAttachmentResponse>> uploadAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        try {
            ReportAttachmentResponse attachment = dailyReportService.uploadAttachment(
                    id, file, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.succes("Attachment berhasil diupload", attachment));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Delete attachment
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(
            @PathVariable Long attachmentId) {
        try {
            dailyReportService.deleteAttachment(attachmentId);
            return ResponseEntity.ok(ApiResponse.succes("Attachment berhasil dihapus", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
