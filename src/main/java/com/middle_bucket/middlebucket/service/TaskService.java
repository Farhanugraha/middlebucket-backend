package com.middle_bucket.middlebucket.service;

import com.middle_bucket.middlebucket.dto.request.TaskCompleteRequest;
import com.middle_bucket.middlebucket.dto.request.TaskRejectRequest;
import com.middle_bucket.middlebucket.dto.request.TaskRequest;
import com.middle_bucket.middlebucket.dto.response.TaskAttachmentResponse;
import com.middle_bucket.middlebucket.dto.response.TaskHistoryResponse;
import com.middle_bucket.middlebucket.dto.response.TaskResponse;
import com.middle_bucket.middlebucket.dto.response.TaskStatsResponse;
import com.middle_bucket.middlebucket.entity.*;
import com.middle_bucket.middlebucket.repository.TaskAttachmentRepository;
import com.middle_bucket.middlebucket.repository.TaskHistoryRepository;
import com.middle_bucket.middlebucket.repository.TaskRepository;
import com.middle_bucket.middlebucket.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskAttachmentRepository taskAttachmentRepository;
    private final TaskHistoryRepository taskHistoryRepository;

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       TaskAttachmentRepository taskAttachmentRepository,
                       TaskHistoryRepository taskHistoryRepository) {
        this.taskRepository           = taskRepository;
        this.userRepository           = userRepository;
        this.taskAttachmentRepository = taskAttachmentRepository;
        this.taskHistoryRepository    = taskHistoryRepository;
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private void recordHistory(Task task, User actor, HistoryAction action,
                               TaskStatus oldStatus, TaskStatus newStatus, String note) {
        TaskHistory history = new TaskHistory();
        history.setTask(task);
        history.setActor(actor);
        history.setAction(action);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setNote(note);
        taskHistoryRepository.save(history);
    }

    // ─── Read ─────────────────────────────────────────────────────────────────

    public List<TaskResponse> getAllTasks(String status) {
        List<Task> tasks;
        if (status != null && !status.isBlank()) {
            tasks = taskRepository.findByStatus(TaskStatus.valueOf(status));
        } else {
            tasks = taskRepository.findAll();
        }
        return tasks.stream().map(TaskResponse::from).toList();
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));
        return TaskResponse.from(task);
    }

    public TaskStatsResponse getStats() {
        return new TaskStatsResponse(
                taskRepository.countByStatus(TaskStatus.TODO),
                taskRepository.countByStatus(TaskStatus.IN_PROGRESS),
                taskRepository.countByStatus(TaskStatus.PENDING_REVIEW),
                taskRepository.countByStatus(TaskStatus.DONE),
                taskRepository.count()
        );
    }

    public List<TaskHistoryResponse> getTaskHistory(Long taskId) {
        taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));
        return taskHistoryRepository.findByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(TaskHistoryResponse::from)
                .toList();
    }

    // ─── Create / Update / Delete ─────────────────────────────────────────────

    @Transactional
    public TaskResponse createTask(TaskRequest request, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (creator.getRole() != Role.MANAGER) {
            throw new RuntimeException("Hanya manager yang dapat membuat task");
        }

        Task task = new Task();
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setPriority(TaskPriority.valueOf(request.getPriority()));
        task.setDueDate(request.getDueDate());
        task.setStatus(TaskStatus.TODO);
        task.setCreatedBy(creator);

        String assigneeInfo = "";
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee tidak ditemukan"));
            task.setAssignee(assignee);
            task.setAssignedAt(LocalDateTime.now());
            assigneeInfo = ", di-assign ke " + assignee.getName();
        }

        Task saved = taskRepository.save(task);

        recordHistory(saved, creator, HistoryAction.CREATED,
                null, TaskStatus.TODO, "Task dibuat" + assigneeInfo);

        return TaskResponse.from(saved);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (user.getRole() != Role.MANAGER) {
            throw new RuntimeException("Hanya manager yang dapat mengubah task");
        }

        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setPriority(TaskPriority.valueOf(request.getPriority()));
        task.setDueDate(request.getDueDate());

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            task.setStatus(TaskStatus.valueOf(request.getStatus()));
        }

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee tidak ditemukan"));
            task.setAssignee(assignee);
            task.setAssignedAt(LocalDateTime.now());
        } else {
            task.setAssignee(null);
            task.setAssignedAt(null);
        }

        Task saved = taskRepository.save(task);

        recordHistory(saved, user, HistoryAction.UPDATED,
                null, saved.getStatus(), "Task diperbarui oleh manager");

        return TaskResponse.from(saved);
    }

    @Transactional
    public void deleteTask(Long id, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (user.getRole() != Role.MANAGER) {
            throw new RuntimeException("Hanya manager yang dapat menghapus task");
        }

        taskRepository.delete(task);
    }

    // ─── Workflow ─────────────────────────────────────────────────────────────

    @Transactional
    public TaskResponse completeTask(Long id, TaskCompleteRequest request, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (task.getAssignee() == null || !task.getAssignee().getId().equals(user.getId())) {
            throw new RuntimeException("Anda tidak berhak menyelesaikan task ini");
        }

        TaskStatus oldStatus = task.getStatus();

        task.setStatus(TaskStatus.PENDING_REVIEW);
        task.setCompletionNote(request.getCompletionNote());
        task.setCompletedBy(user);
        task.setCompletedAt(LocalDateTime.now());

        Task saved = taskRepository.save(task);

        recordHistory(saved, user, HistoryAction.COMPLETED,
                oldStatus, TaskStatus.PENDING_REVIEW, request.getCompletionNote());

        return TaskResponse.from(saved);
    }

    @Transactional
    public TaskResponse approveTask(Long id, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));
        User reviewer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (reviewer.getRole() != Role.MANAGER) {
            throw new RuntimeException("Hanya manager yang dapat menyetujui task");
        }

        task.setStatus(TaskStatus.DONE);
        task.setReviewedBy(reviewer);
        task.setReviewedAt(LocalDateTime.now());

        Task saved = taskRepository.save(task);

        recordHistory(saved, reviewer, HistoryAction.APPROVED,
                TaskStatus.PENDING_REVIEW, TaskStatus.DONE, "Task disetujui");

        return TaskResponse.from(saved);
    }

    @Transactional
    public TaskResponse rejectTask(Long id, TaskRejectRequest request, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));
        User reviewer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (reviewer.getRole() != Role.MANAGER) {
            throw new RuntimeException("Hanya manager yang dapat merevisi task");
        }

        task.setStatus(TaskStatus.TODO);
        task.setRevisionNote(request.getRevisionNote());
        task.setReviewedBy(reviewer);
        task.setReviewedAt(LocalDateTime.now());

        Task saved = taskRepository.save(task);

        recordHistory(saved, reviewer, HistoryAction.REJECTED,
                TaskStatus.PENDING_REVIEW, TaskStatus.TODO, request.getRevisionNote());

        return TaskResponse.from(saved);
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long id, String status, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        TaskStatus oldStatus = task.getStatus();
        TaskStatus newStatus = TaskStatus.valueOf(status);

        if (user.getRole() != Role.MANAGER) {
            if (task.getAssignee() == null || !task.getAssignee().getId().equals(user.getId())) {
                throw new RuntimeException("Anda tidak berhak mengubah status task ini");
            }
            if (newStatus == TaskStatus.DONE) {
                throw new RuntimeException("Task harus melalui proses review terlebih dahulu");
            }
            if (newStatus == TaskStatus.PENDING_REVIEW) {
                throw new RuntimeException("Gunakan tombol Complete untuk menyelesaikan task");
            }
        }

        task.setStatus(newStatus);
        Task saved = taskRepository.save(task);

        recordHistory(saved, user, HistoryAction.STATUS_CHANGED,
                oldStatus, newStatus, null);

        return TaskResponse.from(saved);
    }

    // ─── Attachments ──────────────────────────────────────────────────────────

    @Transactional
    public TaskAttachmentResponse uploadAttachment(Long taskId,
                                                   MultipartFile file,
                                                   String type,
                                                   String uploaderEmail) throws IOException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));
        User uploader = userRepository.findByEmail(uploaderEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        String originalName = file.getOriginalFilename();
        String extension    = originalName.substring(originalName.lastIndexOf("."));
        String filename     = UUID.randomUUID().toString() + extension;

        Path uploadDir = Paths.get("uploads/tasks");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Files.copy(file.getInputStream(), uploadDir.resolve(filename),
                StandardCopyOption.REPLACE_EXISTING);

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task);
        attachment.setFilename(filename);
        attachment.setOriginalName(originalName);
        attachment.setMimeType(file.getContentType());
        attachment.setSize((int) file.getSize());
        attachment.setUploadedBy(uploader);
        attachment.setType(type != null ? AttachmentType.valueOf(type) : AttachmentType.task);

        TaskAttachment saved = taskAttachmentRepository.save(attachment);

        recordHistory(task, uploader, HistoryAction.ATTACHMENT_ADDED,
                null, null, "File ditambahkan: " + originalName);

        return TaskAttachmentResponse.from(saved);
    }

    @Transactional
    public void deleteAttachment(Long attachmentId, String userEmail) throws IOException {
        TaskAttachment attachment = taskAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment tidak ditemukan"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (user.getRole() != Role.MANAGER) {
            throw new RuntimeException("Hanya manager yang dapat menghapus lampiran");
        }

        recordHistory(attachment.getTask(), user, HistoryAction.ATTACHMENT_DELETED,
                null, null, "File dihapus: " + attachment.getOriginalName());

        Path filePath = Paths.get("uploads/tasks/" + attachment.getFilename());
        Files.deleteIfExists(filePath);
        taskAttachmentRepository.delete(attachment);
    }

    @Transactional
    public void deleteEvidenceAttachment(Long attachmentId, String userEmail) throws IOException {
        TaskAttachment attachment = taskAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment tidak ditemukan"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (user.getRole() != Role.MANAGER) {
            if (attachment.getType() != AttachmentType.completion) {
                throw new RuntimeException("Anda hanya dapat menghapus evidence attachment Anda sendiri");
            }
            if (!attachment.getUploadedBy().getId().equals(user.getId())) {
                throw new RuntimeException("Anda hanya dapat menghapus evidence yang Anda upload sendiri");
            }
            Task task = attachment.getTask();
            if (task.getStatus() != TaskStatus.TODO && task.getStatus() != TaskStatus.IN_PROGRESS) {
                throw new RuntimeException("Evidence hanya dapat dihapus saat task belum selesai");
            }
        }

        recordHistory(attachment.getTask(), user, HistoryAction.ATTACHMENT_DELETED,
                null, null, "Evidence dihapus: " + attachment.getOriginalName());

        Path filePath = Paths.get("uploads/tasks/" + attachment.getFilename());
        Files.deleteIfExists(filePath);
        taskAttachmentRepository.delete(attachment);
    }
}