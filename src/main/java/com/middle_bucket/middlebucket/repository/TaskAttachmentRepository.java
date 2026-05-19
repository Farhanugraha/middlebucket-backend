package com.middle_bucket.middlebucket.repository;

import com.middle_bucket.middlebucket.entity.AttachmentType;
import com.middle_bucket.middlebucket.entity.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

    List<TaskAttachment> findByTaskId(Long taskId);
    List<TaskAttachment> findByTaskIdAndType(Long taskId, AttachmentType type);
}
