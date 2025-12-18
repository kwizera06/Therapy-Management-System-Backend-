package com.tms.backend.controller;

import com.tms.backend.dto.MessageResponse;
import com.tms.backend.dto.UserDto;
import com.tms.backend.model.Role;
import com.tms.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('THERAPIST') or hasRole('CLIENT')")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable Role role) {
        logger.info("Getting users by role: {}", role);
        List<UserDto> users = userService.getUsersByRole(role);
        logger.info("Found {} users with role: {}", users.size(), role);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST')")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }
    
    @PutMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> verifyTherapist(@PathVariable Long id) {
        userService.verifyTherapist(id);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return ResponseEntity.ok(new MessageResponse("User status updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }
}
