package com.tms.backend.controller;

import com.tms.backend.dto.GoalDto;
import com.tms.backend.model.Goal.GoalStatus;
import com.tms.backend.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/goals")
public class GoalController {
    
    @Autowired
    private GoalService goalService;
    
    @PostMapping
    @PreAuthorize("hasRole('THERAPIST')")
    public ResponseEntity<GoalDto> createGoal(
            @RequestParam Long clientId,
            @RequestParam Long therapistId,
            @RequestParam String description,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {
        return ResponseEntity.ok(goalService.createGoal(clientId, therapistId, description, startDate, targetDate));
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('THERAPIST')")
    public ResponseEntity<GoalDto> updateGoalStatus(
            @PathVariable Long id,
            @RequestParam GoalStatus status) {
        return ResponseEntity.ok(goalService.updateGoalStatus(id, status));
    }
    
    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<List<GoalDto>> getClientGoals(@PathVariable Long clientId) {
        return ResponseEntity.ok(goalService.getClientGoals(clientId));
    }
}
