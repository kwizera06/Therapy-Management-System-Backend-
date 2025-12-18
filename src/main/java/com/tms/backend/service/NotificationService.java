package com.tms.backend.service;

import com.tms.backend.model.Notification;
import com.tms.backend.model.User;
import com.tms.backend.repository.NotificationRepository;
import com.tms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public void createNotification(Long userId, String message, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        
        notificationRepository.save(notification);
    }
    
    public List<Notification> getUserNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return notificationRepository.findByUserOrderByTimestampDesc(user);
    }
    
    public List<Notification> getUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return notificationRepository.findByUserAndIsReadFalse(user);
    }
    
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
