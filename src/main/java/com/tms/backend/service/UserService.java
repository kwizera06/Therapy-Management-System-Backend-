package com.tms.backend.service;

import com.tms.backend.dto.UserDto;
import com.tms.backend.model.*;
import com.tms.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private TwoFactorTokenRepository twoFactorTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private ResourceRepository resourceRepository;
    
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<UserDto> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }
    
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFullName(userDto.getFullName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setSpecialization(userDto.getSpecialization());
        user.setQualifications(userDto.getQualifications());
        
        User updated = userRepository.save(user);
        return convertToDto(updated);
    }
    
    public void verifyTherapist(Long therapistId) {
        User therapist = userRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));
        
        if (therapist.getRole() != Role.THERAPIST) {
            throw new RuntimeException("User is not a therapist");
        }
        
        therapist.setVerified(true);
        userRepository.save(therapist);
    }
    
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Prevent deleting admin users
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Cannot delete admin users");
        }

        // 1. Delete Tokens
        twoFactorTokenRepository.deleteByUser(user);
        passwordResetTokenRepository.deleteByUser(user);

        // 2. Delete Notifications
        List<Notification> notifications = notificationRepository.findByUserOrderByTimestampDesc(user);
        notificationRepository.deleteAll(notifications);

        // 3. Delete Messages
        List<Message> sentMessages = messageRepository.findBySender(user);
        messageRepository.deleteAll(sentMessages);
        List<Message> receivedMessages = messageRepository.findByReceiver(user);
        messageRepository.deleteAll(receivedMessages);

        // 4. Delete Tasks
        List<Task> clientTasks = taskRepository.findByClient(user);
        taskRepository.deleteAll(clientTasks);
        List<Task> therapistTasks = taskRepository.findByTherapist(user);
        taskRepository.deleteAll(therapistTasks);

        // 5. Delete Goals
        List<Goal> clientGoals = goalRepository.findByClient(user);
        goalRepository.deleteAll(clientGoals);
        List<Goal> therapistGoals = goalRepository.findByTherapist(user);
        goalRepository.deleteAll(therapistGoals);

        // 5.5 Delete Resources
        List<Resource> clientResources = resourceRepository.findByClient(user);
        resourceRepository.deleteAll(clientResources);
        List<Resource> therapistResources = resourceRepository.findByTherapist(user);
        resourceRepository.deleteAll(therapistResources);

        // 6. Delete Appointments (and related Sessions and Payments)
        List<Appointment> clientAppointments = appointmentRepository.findByClient(user);
        deleteAppointments(clientAppointments);

        List<Appointment> therapistAppointments = appointmentRepository.findByTherapist(user);
        deleteAppointments(therapistAppointments);

        // 7. Delete Direct Payments (if any not linked to session)
        List<Payment> payments = paymentRepository.findByClientId(userId);
        paymentRepository.deleteAll(payments);
        
        userRepository.delete(user);
    }

    private void deleteAppointments(List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            // Find session
            Optional<Session> sessionOpt = sessionRepository.findByAppointment(appointment);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                
                // Find and delete payment associated with this session
                Optional<Payment> paymentOpt = paymentRepository.findBySession(session);
                if (paymentOpt.isPresent()) {
                    paymentRepository.delete(paymentOpt.get());
                }
                
                sessionRepository.delete(session);
            }
            appointmentRepository.delete(appointment);
        }
    }
    
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setSpecialization(user.getSpecialization());
        dto.setQualifications(user.getQualifications());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setVerified(user.isVerified());
        dto.setEnabled(user.isEnabled());
        return dto;
    }
}
