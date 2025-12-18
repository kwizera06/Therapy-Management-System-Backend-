package com.tms.backend.service;

import com.tms.backend.dto.GoalDto;
import com.tms.backend.model.Goal;
import com.tms.backend.model.Goal.GoalStatus;
import com.tms.backend.model.User;
import com.tms.backend.repository.GoalRepository;
import com.tms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {
    
    @Autowired
    private GoalRepository goalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public GoalDto createGoal(Long clientId, Long therapistId, String description, 
                              LocalDate startDate, LocalDate targetDate) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        User therapist = userRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));
        
        Goal goal = new Goal();
        goal.setClient(client);
        goal.setTherapist(therapist);
        goal.setDescription(description);
        goal.setStatus(GoalStatus.IN_PROGRESS);
        goal.setStartDate(startDate);
        goal.setTargetDate(targetDate);
        
        Goal saved = goalRepository.save(goal);
        
        // Notify client
        notificationService.createNotification(clientId, 
            "New therapy goal has been set", "SYSTEM");
        
        return convertToDto(saved);
    }
    
    public GoalDto updateGoalStatus(Long goalId, GoalStatus status) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        
        goal.setStatus(status);
        Goal updated = goalRepository.save(goal);
        
        return convertToDto(updated);
    }
    
    public List<GoalDto> getClientGoals(Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        
        return goalRepository.findByClient(client).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private GoalDto convertToDto(Goal goal) {
        GoalDto dto = new GoalDto();
        dto.setId(goal.getId());
        dto.setClientId(goal.getClient().getId());
        dto.setTherapistId(goal.getTherapist().getId());
        dto.setDescription(goal.getDescription());
        dto.setStatus(goal.getStatus());
        dto.setStartDate(goal.getStartDate());
        dto.setTargetDate(goal.getTargetDate());
        return dto;
    }
}
