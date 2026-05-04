package com.middle_bucket.middlebucket.repository;

import com.middle_bucket.middlebucket.entity.Memo;
import com.middle_bucket.middlebucket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {

    List<Memo> findAllByOrderByCreatedAtDesc();
    Optional<Memo> findByMemoNumber(String memoNumber);
    boolean existsByMemoNumber(String memoNumber);
    List<Memo> findByAuthor(User author);

}
