package com.tms.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne
    @JoinColumn(name = "therapist_id", nullable = false)
    private User therapist;

    private String description;

    @Enumerated(EnumType.STRING)
    private GoalStatus status; // IN_PROGRESS, COMPLETED

    private LocalDate startDate;
    private LocalDate targetDate;

    public enum GoalStatus {
        IN_PROGRESS, COMPLETED
    }
}
