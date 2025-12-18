package com.tms.backend.service;

import com.tms.backend.dto.TaskDto;
import com.tms.backend.model.Goal;
import com.tms.backend.model.Task;
import com.tms.backend.model.User;
import com.tms.backend.repository.GoalRepository;
import com.tms.backend.repository.TaskRepository;
import com.tms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GoalRepository goalRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public TaskDto createTask(Long clientId, Long therapistId, Long goalId, 
                              String title, String instructions, LocalDate dueDate) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        User therapist = userRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));
        
        Task task = new Task();
        task.setClient(client);
        task.setTherapist(therapist);
        task.setTitle(title);
        task.setInstructions(instructions);
        task.setDueDate(dueDate);
        task.setCompleted(false);
        
        if (goalId != null) {
            Goal goal = goalRepository.findById(goalId)
                    .orElseThrow(() -> new RuntimeException("Goal not found"));
            task.setGoal(goal);
        }
        
        Task saved = taskRepository.save(task);
        
        // Notify client
        notificationService.createNotification(clientId, 
            "New task assigned: " + title, "TASK");
        
        return convertToDto(saved);
    }
    
    public TaskDto markTaskCompleted(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        task.setCompleted(true);
        Task updated = taskRepository.save(task);
        
        // Notify therapist
        notificationService.createNotification(task.getTherapist().getId(), 
            task.getClient().getFullName() + " completed task: " + task.getTitle(), "TASK");
        
        return convertToDto(updated);
    }
    
    public List<TaskDto> getClientTasks(Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        
        return taskRepository.findByClient(client).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<TaskDto> getGoalTasks(Long goalId) {
        return taskRepository.findByGoalId(goalId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private TaskDto convertToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setGoalId(task.getGoal() != null ? task.getGoal().getId() : null);
        dto.setClientId(task.getClient().getId());
        dto.setTherapistId(task.getTherapist().getId());
        dto.setTitle(task.getTitle());
        dto.setInstructions(task.getInstructions());
        dto.setDueDate(task.getDueDate());
        dto.setCompleted(task.isCompleted());
        return dto;
    }
}
