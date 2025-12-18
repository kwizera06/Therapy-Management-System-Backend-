package com.tms.backend.dto;

import java.util.Set;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String fullName;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
    
    private Set<String> role;
    
    // Optional fields for Therapist
    private String specialization;
    private String qualifications;
    
    // Optional fields for Client
    private String phoneNumber;
}
