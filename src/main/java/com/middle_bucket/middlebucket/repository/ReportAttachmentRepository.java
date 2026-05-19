package com.middle_bucket.middlebucket.repository;

import com.middle_bucket.middlebucket.entity.ReportAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportAttachmentRepository extends JpaRepository<ReportAttachment, Long> {
    List<ReportAttachment> findByReportId(Long reportId);
}
