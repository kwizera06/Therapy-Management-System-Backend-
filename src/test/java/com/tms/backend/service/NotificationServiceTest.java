package com.tms.backend.service;

import com.tms.backend.model.Notification;
import com.tms.backend.model.User;
import com.tms.backend.repository.NotificationRepository;
import com.tms.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        notification = new Notification();
        notification.setId(1L);
        notification.setUser(user);
        notification.setMessage("Test Message");
        notification.setRead(false);
    }

    @Test
    void createNotification_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.createNotification(1L, "Test Message", "INFO");

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void markAsRead_Success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.markAsRead(1L);

        assertTrue(notification.isRead());
        verify(notificationRepository, times(1)).save(notification);
    }
}
