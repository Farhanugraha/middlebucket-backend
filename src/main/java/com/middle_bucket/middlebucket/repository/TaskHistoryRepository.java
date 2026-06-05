package com.middle_bucket.middlebucket.repository;

import com.middle_bucket.middlebucket.entity.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    List<TaskHistory> findByTaskIdOrderByCreatedAtAsc(Long taskId);
}
