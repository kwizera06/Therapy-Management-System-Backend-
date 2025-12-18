package com.tms.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "therapist_id", nullable = false)
    private User therapist;

    private String title;
    private String description;
    private String fileUrl; // URL to cloud storage or local path
    private String fileType; // PDF, AUDIO, VIDEO, etc.

    // Optional: If resource is specific to a client
    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client; 
}
