package com.tms.backend.controller;

import com.tms.backend.model.Notification;
import com.tms.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }
    
    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }
    
    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
