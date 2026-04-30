package com.middle_bucket.middlebucket.repository;

import com.middle_bucket.middlebucket.entity.TaskStatus;
import com.middle_bucket.middlebucket.entity.User;
import com.middle_bucket.middlebucket.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);

    List<Task> findByAssignee(User assignee);

    List<Task> findByCreatedBy(User createdBy);

    List<Task> findByAssigneeAndStatus(User assignee, TaskStatus status);

    // Untuk stats dashboard
    long countByStatus(TaskStatus status);

    long countByAssignee(User assignee);

    long countByAssigneeAndStatus(User assignee, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.assignee.id = :userId OR t.createdBy.id = :userId")
    List<Task> findByAssigneeIdOrCreatedById(Integer userId);
}
