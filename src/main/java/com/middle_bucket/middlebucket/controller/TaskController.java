package com.middle_bucket.middlebucket.controller;

import com.middle_bucket.middlebucket.dto.request.TaskCompleteRequest;
import com.middle_bucket.middlebucket.dto.request.TaskRejectRequest;
import com.middle_bucket.middlebucket.dto.request.TaskRequest;
import com.middle_bucket.middlebucket.dto.response.ApiResponse;
import com.middle_bucket.middlebucket.dto.response.TaskAttachmentResponse;
import com.middle_bucket.middlebucket.dto.response.TaskHistoryResponse;
import com.middle_bucket.middlebucket.dto.response.TaskResponse;
import com.middle_bucket.middlebucket.dto.response.TaskStatsResponse;
import com.middle_bucket.middlebucket.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllTasks(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.succes("Berhasil mengambil tasks",
                taskService.getAllTasks(status)));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<TaskStatsResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.succes("Berhasil mengambil stats",
                taskService.getStats()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.succes("Berhasil",
                    taskService.getTaskById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<TaskHistoryResponse>>> getTaskHistory(
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.succes("Berhasil mengambil history",
                    taskService.getTaskHistory(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @RequestBody TaskRequest request,
            Authentication authentication) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.succes("Task berhasil dibuat",
                            taskService.createTask(request, authentication.getName())));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequest request,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(ApiResponse.succes("Task berhasil diupdate",
                    taskService.updateTask(id, request, authentication.getName())));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            taskService.deleteTask(id, authentication.getName());
            return ResponseEntity.ok(ApiResponse.succes("Task berhasil dihapus", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<TaskResponse>> completeTask(
            @PathVariable Long id,
            @RequestBody TaskCompleteRequest request,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(ApiResponse.succes("Task berhasil diselesaikan",
                    taskService.completeTask(id, request, authentication.getName())));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<TaskResponse>> approveTask(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(ApiResponse.succes("Task berhasil diapprove",
                    taskService.approveTask(id, authentication.getName())));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<TaskResponse>> rejectTask(
            @PathVariable Long id,
            @RequestBody TaskRejectRequest request,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(ApiResponse.succes("Task berhasil direject",
                    taskService.rejectTask(id, request, authentication.getName())));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(ApiResponse.succes("Status berhasil diupdate",
                    taskService.updateTaskStatus(id, payload.get("status"),
                            authentication.getName())));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<ApiResponse<List<TaskAttachmentResponse>>> uploadAttachments(
            @PathVariable Long id,
            @RequestParam("attachments") List<MultipartFile> files,
            @RequestParam(value = "type", defaultValue = "task") String type,
            Authentication authentication) {
        try {
            List<TaskAttachmentResponse> attachments = files.stream()
                    .map(file -> {
                        try {
                            return taskService.uploadAttachment(id, file, type,
                                    authentication.getName());
                        } catch (IOException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    })
                    .toList();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.succes("Attachment berhasil diupload", attachments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(
            @PathVariable Long attachmentId,
            Authentication authentication) {
        try {
            taskService.deleteAttachment(attachmentId, authentication.getName());
            return ResponseEntity.ok(ApiResponse.succes("Attachment berhasil dihapus", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/attachments/{attachmentId}/evidence")
    public ResponseEntity<ApiResponse<Void>> deleteEvidenceAttachment(
            @PathVariable Long attachmentId,
            Authentication authentication) {
        try {
            taskService.deleteEvidenceAttachment(attachmentId, authentication.getName());
            return ResponseEntity.ok(ApiResponse.succes("Evidence berhasil dihapus", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}