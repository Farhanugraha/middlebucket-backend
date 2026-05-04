package com.middle_bucket.middlebucket.service;


import com.middle_bucket.middlebucket.dto.request.MemoRequest;
import com.middle_bucket.middlebucket.dto.response.MemoAttachmentResponse;
import com.middle_bucket.middlebucket.dto.response.MemoResponse;
import com.middle_bucket.middlebucket.entity.Memo;
import com.middle_bucket.middlebucket.entity.MemoAttachment;
import com.middle_bucket.middlebucket.entity.User;
import com.middle_bucket.middlebucket.repository.MemoAttachmentRepository;
import com.middle_bucket.middlebucket.repository.MemoRepository;
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
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserRepository userRepository;
    private final MemoAttachmentRepository memoAttachmentRepository;

    public MemoService(MemoRepository memoRepository, UserRepository userRepository,
                       MemoAttachmentRepository memoAttachmentRepository) {
        this.memoRepository = memoRepository;
        this.userRepository = userRepository;
        this.memoAttachmentRepository = memoAttachmentRepository;
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

//    Upload Attachment
    @Transactional
    public MemoAttachmentResponse uploadAttachment(Long memoId, MultipartFile file,
                                                   String uploaderEmail) throws IOException {

        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new RuntimeException("Memo tidak ditemukan"));

        User uploader = userRepository.findByEmail(uploaderEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // Create unique file name
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        //Save file to folder uploads/memos
        Path uploadDir = Paths.get("uploads/memos");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Files.copy(file.getInputStream(),
                uploadDir.resolve(filename),
                StandardCopyOption.REPLACE_EXISTING);

        // Save metadata to database
        MemoAttachment attachment = new MemoAttachment();
        attachment.setMemo(memo);
        attachment.setFilename(filename);
        attachment.setOriginalName(originalName);
        attachment.setMimeType(file.getContentType());
        attachment.setSize((int) file.getSize());
        attachment.setUploadedBy(uploader);

        return MemoAttachmentResponse.from(memoAttachmentRepository.save(attachment));
    }

//    Delete Attachment
    @Transactional
    public void deleteAttachment(Long attachmentId) throws IOException {
        MemoAttachment attachment = memoAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment tidak ditemukan"));

        // Remove file from storage
        Path filePath = Paths.get("uploads/memos/" + attachment.getFilename());
        Files.deleteIfExists(filePath);

        // Remove from database
        memoAttachmentRepository.delete(attachment);
    }
}
