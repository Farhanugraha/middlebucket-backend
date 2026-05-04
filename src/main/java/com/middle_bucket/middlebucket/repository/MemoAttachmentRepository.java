package com.middle_bucket.middlebucket.repository;

import com.middle_bucket.middlebucket.entity.MemoAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoAttachmentRepository extends JpaRepository<MemoAttachment, Long> {
    List<MemoAttachment> findByMemoId(Long memoId);
}
