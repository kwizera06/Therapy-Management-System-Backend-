package com.tms.backend.controller;

import com.tms.backend.dto.MessageDto;
import com.tms.backend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @PostMapping
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST')")
    public ResponseEntity<MessageDto> sendMessage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String content,
            @RequestParam(defaultValue = "TEXT") String type) {
        return ResponseEntity.ok(messageService.sendMessage(senderId, receiverId, content, type));
    }
    
    @GetMapping("/conversation")
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST')")
    public ResponseEntity<List<MessageDto>> getConversation(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id) {
        return ResponseEntity.ok(messageService.getConversation(user1Id, user2Id));
    }
    
    @GetMapping("/unread/{userId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST')")
    public ResponseEntity<List<MessageDto>> getUnreadMessages(@PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getUnreadMessages(userId));
    }
    
    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
