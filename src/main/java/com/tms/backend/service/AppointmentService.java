package com.tms.backend.service;

import com.tms.backend.dto.AppointmentDto;
import com.tms.backend.model.Appointment;
import com.tms.backend.model.Appointment.AppointmentStatus;
import com.tms.backend.model.User;
import com.tms.backend.repository.AppointmentRepository;
import com.tms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public AppointmentDto createAppointment(Long clientId, Long therapistId, LocalDateTime appointmentTime) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        User therapist = userRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));
        
        Appointment appointment = new Appointment();
        appointment.setClient(client);
        appointment.setTherapist(therapist);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus(AppointmentStatus.PENDING);
        
        Appointment saved = appointmentRepository.save(appointment);
        
        // Notify therapist
        notificationService.createNotification(therapistId, 
            "New appointment request from " + client.getFullName(), "APPOINTMENT");
        
        return convertToDto(saved);
    }
    
    public AppointmentDto updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        appointment.setStatus(status);
        Appointment updated = appointmentRepository.save(appointment);
        
        // Notify client
        String message = status == AppointmentStatus.APPROVED ? 
            "Your appointment has been approved" : "Your appointment has been rejected";
        notificationService.createNotification(appointment.getClient().getId(), message, "APPOINTMENT");
        
        return convertToDto(updated);
    }
    
    public List<AppointmentDto> getClientAppointments(Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        
        return appointmentRepository.findByClient(client).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<AppointmentDto> getTherapistAppointments(Long therapistId) {
        User therapist = userRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));
        
        return appointmentRepository.findByTherapist(therapist).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private AppointmentDto convertToDto(Appointment appointment) {
        AppointmentDto dto = new AppointmentDto();
        dto.setId(appointment.getId());
        dto.setClientId(appointment.getClient().getId());
        dto.setClientName(appointment.getClient().getFullName());
        dto.setTherapistId(appointment.getTherapist().getId());
        dto.setTherapistName(appointment.getTherapist().getFullName());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setStatus(appointment.getStatus());
        return dto;
    }
}
