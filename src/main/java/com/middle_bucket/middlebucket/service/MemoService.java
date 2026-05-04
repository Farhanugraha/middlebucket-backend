package com.middle_bucket.middlebucket.service;


import com.middle_bucket.middlebucket.dto.request.MemoRequest;
import com.middle_bucket.middlebucket.dto.response.MemoResponse;
import com.middle_bucket.middlebucket.entity.Memo;
import com.middle_bucket.middlebucket.entity.User;
import com.middle_bucket.middlebucket.repository.MemoRepository;
import com.middle_bucket.middlebucket.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserRepository userRepository;

    public MemoService(MemoRepository memoRepository, UserRepository userRepository) {
        this.memoRepository = memoRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<MemoResponse> getAllMemos() {
        return memoRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(MemoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MemoResponse getMemoById(Long id) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Memo tidak ditemukan"));
        return MemoResponse.from(memo);
    }


//    Create Memo
    @Transactional
    public MemoResponse createMemo(MemoRequest request, String authorEmail) {

        if (memoRepository.existsByMemoNumber(request.getMemoNumber())) {
            throw new RuntimeException("Memo number sudah digunakan: " + request.getMemoNumber());
        }

        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        LocalDate memoDate = LocalDate.parse(request.getMemoDate());

        Memo memo = new Memo();
        memo.setMemoNumber(request.getMemoNumber());
        memo.setMemoDate(memoDate);
        memo.setMemoFrom(request.getMemoFrom());
        memo.setShortDescription(request.getShortDescription());
        memo.setDescription(request.getDescription());
        memo.setAuthor(author);

        return MemoResponse.from(memoRepository.save(memo));
    }

//    Delete Memo
    @Transactional
    public void deleteMemo(Long id) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Memo tidak ditemukan"));
        memoRepository.delete(memo);
    }
}
