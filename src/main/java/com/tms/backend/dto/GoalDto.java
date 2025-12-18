package com.tms.backend.dto;

import com.tms.backend.model.Goal.GoalStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class GoalDto {
    private Long id;
    private Long clientId;
    private Long therapistId;
    private String description;
    private GoalStatus status;
    private LocalDate startDate;
    private LocalDate targetDate;
}
