package com.tms.backend.service;

import com.tms.backend.dto.UserDto;
import com.tms.backend.model.Role;
import com.tms.backend.model.User;
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
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setRole(Role.CLIENT);
        user.setEnabled(true);
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
    }

    @Test
    void verifyTherapist_Success() {
        user.setRole(Role.THERAPIST);
        user.setVerified(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.verifyTherapist(1L);

        assertTrue(user.isVerified());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void toggleUserStatus_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.toggleUserStatus(1L);

        assertFalse(user.isEnabled());
        verify(userRepository, times(1)).save(user);
    }
}
