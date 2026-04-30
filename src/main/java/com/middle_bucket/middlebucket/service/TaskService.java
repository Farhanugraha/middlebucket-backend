package com.middle_bucket.middlebucket.service;


import com.middle_bucket.middlebucket.dto.request.TaskCompleteRequest;
import com.middle_bucket.middlebucket.dto.request.TaskRejectRequest;
import com.middle_bucket.middlebucket.dto.request.TaskRequest;
import com.middle_bucket.middlebucket.dto.response.TaskResponse;
import com.middle_bucket.middlebucket.dto.response.TaskStatsResponse;
import com.middle_bucket.middlebucket.entity.Task;
import com.middle_bucket.middlebucket.entity.TaskPriority;
import com.middle_bucket.middlebucket.entity.TaskStatus;
import com.middle_bucket.middlebucket.entity.User;
import com.middle_bucket.middlebucket.repository.TaskRepository;
import com.middle_bucket.middlebucket.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

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

    @Transactional
    public TaskResponse createTask(TaskRequest request, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Task task = new Task();
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setPriority(TaskPriority.valueOf(request.getPriority()));
        task.setDueDate(request.getDueDate());
        task.setStatus(TaskStatus.TODO);
        task.setCreatedBy(creator);

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee tidak ditemukan"));
            task.setAssignee(assignee);
            task.setAssignedAt(LocalDateTime.now());
        }

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));

        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setPriority(TaskPriority.valueOf(request.getPriority()));
        task.setDueDate(request.getDueDate());

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee tidak ditemukan"));
            task.setAssignee(assignee);
            task.setAssignedAt(LocalDateTime.now());
        }

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));
        taskRepository.delete(task);
    }

    @Transactional
    public TaskResponse completeTask(Long id, TaskCompleteRequest request, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        task.setStatus(TaskStatus.PENDING_REVIEW);
        task.setCompletionNote(request.getCompletionNote());
        task.setCompletedBy(user);
        task.setCompletedAt(LocalDateTime.now());

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse approveTask(Long id, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));
        User reviewer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        task.setStatus(TaskStatus.DONE);
        task.setReviewedBy(reviewer);
        task.setReviewedAt(LocalDateTime.now());

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse rejectTask(Long id, TaskRejectRequest request, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));
        User reviewer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setRevisionNote(request.getRevisionNote());
        task.setReviewedBy(reviewer);
        task.setReviewedAt(LocalDateTime.now());

        return TaskResponse.from(taskRepository.save(task));
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



}
