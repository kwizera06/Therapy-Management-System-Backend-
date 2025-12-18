package com.tms.backend.service;

import com.tms.backend.dto.SessionDto;
import com.tms.backend.model.Appointment;
import com.tms.backend.model.Session;
import com.tms.backend.repository.AppointmentRepository;
import com.tms.backend.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public SessionDto createSession(Long appointmentId, String notes, String summary, Integer progressScore) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        Session session = new Session();
        session.setAppointment(appointment);
        session.setNotes(notes);
        session.setSummary(summary);
        session.setProgressScore(progressScore);
        
        Session saved = sessionRepository.save(session);
        
        // Notify client
        notificationService.createNotification(appointment.getClient().getId(), 
            "Session notes have been added", "SYSTEM");
        
        return convertToDto(saved);
    }
    
    public SessionDto updateSession(Long sessionId, String notes, String summary, Integer progressScore) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        session.setNotes(notes);
        session.setSummary(summary);
        session.setProgressScore(progressScore);
        
        Session updated = sessionRepository.save(session);
        return convertToDto(updated);
    }
    
    public List<SessionDto> getClientSessions(Long clientId) {
        return sessionRepository.findByAppointment_ClientId(clientId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<SessionDto> getTherapistSessions(Long therapistId) {
        return sessionRepository.findByAppointment_TherapistId(therapistId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private SessionDto convertToDto(Session session) {
        SessionDto dto = new SessionDto();
        dto.setId(session.getId());
        dto.setAppointmentId(session.getAppointment().getId());
        dto.setNotes(session.getNotes());
        dto.setSummary(session.getSummary());
        dto.setProgressScore(session.getProgressScore());
        dto.setCreatedAt(session.getCreatedAt());
        return dto;
    }
}
