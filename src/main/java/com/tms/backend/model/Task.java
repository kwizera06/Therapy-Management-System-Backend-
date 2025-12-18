package com.tms.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;
    
    @ManyToOne
    @JoinColumn(name = "therapist_id", nullable = false)
    private User therapist;

    private String title;
    private String instructions;

    private LocalDate dueDate;
    private boolean completed = false;
}
