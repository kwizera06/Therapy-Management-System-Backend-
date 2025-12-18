package com.tms.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskDto {
    private Long id;
    private Long goalId;
    private Long clientId;
    private Long therapistId;
    private String title;
    private String instructions;
    private LocalDate dueDate;
    private boolean completed;
}
