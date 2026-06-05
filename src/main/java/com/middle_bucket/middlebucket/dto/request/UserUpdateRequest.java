package com.middle_bucket.middlebucket.dto.request;

import com.middle_bucket.middlebucket.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
    private Role role;
}