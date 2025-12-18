package com.tms.backend.controller;

import com.tms.backend.dto.AppointmentDto;
import com.tms.backend.model.Appointment.AppointmentStatus;
import com.tms.backend.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    
    @Autowired
    private AppointmentService appointmentService;
    
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<AppointmentDto> createAppointment(
            @RequestParam Long clientId,
            @RequestParam Long therapistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentTime) {
        return ResponseEntity.ok(appointmentService.createAppointment(clientId, therapistId, appointmentTime));
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('THERAPIST')")
    public ResponseEntity<AppointmentDto> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, status));
    }
    
    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentDto>> getClientAppointments(@PathVariable Long clientId) {
        return ResponseEntity.ok(appointmentService.getClientAppointments(clientId));
    }
    
    @GetMapping("/therapist/{therapistId}")
    @PreAuthorize("hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentDto>> getTherapistAppointments(@PathVariable Long therapistId) {
        return ResponseEntity.ok(appointmentService.getTherapistAppointments(therapistId));
    }
}
