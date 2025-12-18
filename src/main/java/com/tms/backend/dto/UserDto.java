package com.tms.backend.dto;

import com.tms.backend.model.Role;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private String specialization;
    private String qualifications;
    private String phoneNumber;
    private boolean isVerified;
    private boolean enabled;
}
