package com.middle_bucket.middlebucket.service;


import com.middle_bucket.middlebucket.dto.request.DailyReportRequest;
import com.middle_bucket.middlebucket.dto.response.DailyReportResponse;
import com.middle_bucket.middlebucket.dto.response.ReportAttachmentResponse;
import com.middle_bucket.middlebucket.entity.DailyReport;
import com.middle_bucket.middlebucket.entity.ReportAttachment;
import com.middle_bucket.middlebucket.entity.User;
import com.middle_bucket.middlebucket.repository.DailyReportRepository;
import com.middle_bucket.middlebucket.repository.ReportAttachmentRepository;
import com.middle_bucket.middlebucket.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DailyReportService {
    private final DailyReportRepository dailyReportRepository;
    private final UserRepository userRepository;
    private final ReportAttachmentRepository reportAttachmentRepository;

    public DailyReportService(DailyReportRepository dailyReportRepository,
                              UserRepository userRepository,
                              ReportAttachmentRepository reportAttachmentRepository) {
        this.dailyReportRepository = dailyReportRepository;
        this.userRepository = userRepository;
        this.reportAttachmentRepository = reportAttachmentRepository;
    }

    // Get all reports
    @Transactional(readOnly = true)
    public List<DailyReportResponse> getAllReports() {
        return dailyReportRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(DailyReportResponse::from)
                .toList();
    }

    // Get reports by user
    @Transactional(readOnly = true)
    public List<DailyReportResponse> getReportsByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        return dailyReportRepository.findByUser(user)
                .stream()
                .map(DailyReportResponse::from)
                .toList();
    }

    // Get report by id
    @Transactional(readOnly = true)
    public DailyReportResponse getReportById(Long id) {
        DailyReport report = dailyReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report tidak ditemukan"));
        return DailyReportResponse.from(report);
    }

    // Create report
    @Transactional
    public DailyReportResponse createReport(DailyReportRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        LocalDate reportDate = LocalDate.parse(request.getReportDate());

        // Validate 1 user only 1 report per day
        if (dailyReportRepository.existsByUserAndReportDate(user, reportDate)) {
            throw new RuntimeException("Anda sudah membuat report untuk tanggal " + reportDate);
        }

        DailyReport report = new DailyReport();
        report.setUser(user);
        report.setReportDate(reportDate);
        report.setContent(request.getContent());

        return DailyReportResponse.from(dailyReportRepository.save(report));
    }

    // Upload attachment
    @Transactional
    public ReportAttachmentResponse uploadAttachment(Long reportId,
                                                     MultipartFile file,
                                                     String uploaderEmail) throws IOException {
        DailyReport report = dailyReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report tidak ditemukan"));

        User uploader = userRepository.findByEmail(uploaderEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // Create Unique file name
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // Save file to folder uploads/reports
        Path uploadDir = Paths.get("uploads/reports");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Files.copy(file.getInputStream(),
                uploadDir.resolve(filename),
                StandardCopyOption.REPLACE_EXISTING);

        // Save metadata to database
        ReportAttachment attachment = new ReportAttachment();
        attachment.setReport(report);
        attachment.setFilename(filename);
        attachment.setOriginalName(originalName);
        attachment.setMimeType(file.getContentType());
        attachment.setSize((int) file.getSize());
        attachment.setUploadedBy(uploader);

        return ReportAttachmentResponse.from(reportAttachmentRepository.save(attachment));
    }


    // Delete attachment
    @Transactional
    public void deleteAttachment(Long attachmentId) throws IOException {
        ReportAttachment attachment = reportAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment tidak ditemukan"));

        // Delete file from storage
        Path filePath = Paths.get("uploads/reports/" + attachment.getFilename());
        Files.deleteIfExists(filePath);

        // Delete from database
        reportAttachmentRepository.delete(attachment);
    }
}
