package com.tms.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SessionDto {
    private Long id;
    private Long appointmentId;
    private String notes;
    private String summary;
    private Integer progressScore;
    private LocalDateTime createdAt;
}
