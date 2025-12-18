package com.tms.backend.dto;

import com.tms.backend.model.Appointment.AppointmentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentDto {
    private Long id;
    private Long clientId;
    private String clientName;
    private Long therapistId;
    private String therapistName;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
}
