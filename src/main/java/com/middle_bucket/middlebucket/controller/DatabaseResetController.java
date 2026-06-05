package com.middle_bucket.middlebucket.controller;

import com.middle_bucket.middlebucket.dto.response.ApiResponse;
import com.middle_bucket.middlebucket.service.DatabaseResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class DatabaseResetController {

    @Autowired
    private DatabaseResetService databaseResetService;

    @PostMapping("/reset-database")
    public ResponseEntity<ApiResponse<String>> resetDatabase() {
        try {
            databaseResetService.resetDatabase();
            return ResponseEntity.ok(ApiResponse.succes("Database berhasil direset!", null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Gagal reset database: " + e.getMessage()));
        }
    }
}