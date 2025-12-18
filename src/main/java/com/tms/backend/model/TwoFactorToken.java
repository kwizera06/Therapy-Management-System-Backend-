package com.tms.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "two_factor_tokens")
@Data
@NoArgsConstructor
public class TwoFactorToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String code;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    
    private boolean verified = false;
    
    private int attempts = 0;
    
    public TwoFactorToken(String code, User user) {
        this.code = code;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusMinutes(10); // Code expires in 10 minutes
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
    
    public boolean isMaxAttemptsReached() {
        return attempts >= 3;
    }
    
    public void incrementAttempts() {
        this.attempts++;
    }
}
