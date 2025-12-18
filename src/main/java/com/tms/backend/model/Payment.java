package com.tms.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @OneToOne
    @JoinColumn(name = "session_id")
    private Session session;

    private BigDecimal amount;
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // PENDING, COMPLETED, FAILED

    private String transactionId; // From payment gateway
    private LocalDateTime paymentDate;

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED
    }
}
