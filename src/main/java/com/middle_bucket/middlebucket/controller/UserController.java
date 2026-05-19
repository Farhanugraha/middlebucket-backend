package com.middle_bucket.middlebucket.controller;


import com.middle_bucket.middlebucket.dto.response.ApiResponse;
import com.middle_bucket.middlebucket.dto.response.UserResponse;
import com.middle_bucket.middlebucket.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
      this.userService = userService;
  }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.succes("Berhasil mengambil users", users));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        try {
            UserResponse user = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.succes("Berhasil", user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
