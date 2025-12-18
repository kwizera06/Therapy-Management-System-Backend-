package com.tms.backend.controller;

import com.tms.backend.dto.SessionDto;
import com.tms.backend.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    
    @Autowired
    private SessionService sessionService;
    
    @PostMapping
    @PreAuthorize("hasRole('THERAPIST')")
    public ResponseEntity<SessionDto> createSession(
            @RequestParam Long appointmentId,
            @RequestParam String notes,
            @RequestParam String summary,
            @RequestParam Integer progressScore) {
        return ResponseEntity.ok(sessionService.createSession(appointmentId, notes, summary, progressScore));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('THERAPIST')")
    public ResponseEntity<SessionDto> updateSession(
            @PathVariable Long id,
            @RequestParam String notes,
            @RequestParam String summary,
            @RequestParam Integer progressScore) {
        return ResponseEntity.ok(sessionService.updateSession(id, notes, summary, progressScore));
    }
    
    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<List<SessionDto>> getClientSessions(@PathVariable Long clientId) {
        return ResponseEntity.ok(sessionService.getClientSessions(clientId));
    }
    
    @GetMapping("/therapist/{therapistId}")
    @PreAuthorize("hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<List<SessionDto>> getTherapistSessions(@PathVariable Long therapistId) {
        return ResponseEntity.ok(sessionService.getTherapistSessions(therapistId));
    }
}
