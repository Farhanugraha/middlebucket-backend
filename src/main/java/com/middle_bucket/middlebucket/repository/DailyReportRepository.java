package com.middle_bucket.middlebucket.repository;

import com.middle_bucket.middlebucket.entity.DailyReport;
import com.middle_bucket.middlebucket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
    List<DailyReport> findAllByOrderByCreatedAtDesc();
    List<DailyReport> findByUser(User user);
    List<DailyReport> findByReportDate(LocalDate reportDate);
    Optional<DailyReport> findByUserAndReportDate(User user, LocalDate reportDate);
    boolean existsByUserAndReportDate(User user, LocalDate reportDate);
}