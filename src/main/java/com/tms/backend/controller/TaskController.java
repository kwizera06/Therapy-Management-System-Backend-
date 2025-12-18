package com.tms.backend.controller;

import com.tms.backend.dto.TaskDto;
import com.tms.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @PostMapping
    @PreAuthorize("hasRole('THERAPIST')")
    public ResponseEntity<TaskDto> createTask(
            @RequestParam Long clientId,
            @RequestParam Long therapistId,
            @RequestParam(required = false) Long goalId,
            @RequestParam String title,
            @RequestParam String instructions,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        return ResponseEntity.ok(taskService.createTask(clientId, therapistId, goalId, title, instructions, dueDate));
    }
    
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<TaskDto> markTaskCompleted(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.markTaskCompleted(id));
    }
    
    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<List<TaskDto>> getClientTasks(@PathVariable Long clientId) {
        return ResponseEntity.ok(taskService.getClientTasks(clientId));
    }
    
    @GetMapping("/goal/{goalId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<List<TaskDto>> getGoalTasks(@PathVariable Long goalId) {
        return ResponseEntity.ok(taskService.getGoalTasks(goalId));
    }
}
