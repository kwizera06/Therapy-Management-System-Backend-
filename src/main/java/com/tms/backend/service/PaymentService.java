package com.tms.backend.service;

import com.tms.backend.model.Payment;
import com.tms.backend.model.Session;
import com.tms.backend.model.User;
import com.tms.backend.repository.PaymentRepository;
import com.tms.backend.repository.SessionRepository;
import com.tms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SessionRepository sessionRepository;
    
    public Payment createPayment(Long clientId, Long sessionId, BigDecimal amount, String currency, String transactionId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        
        Payment payment = new Payment();
        payment.setClient(client);
        payment.setAmount(amount);
        payment.setCurrency(currency != null ? currency : "USD");
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setTransactionId(transactionId);
        payment.setPaymentDate(LocalDateTime.now());
        
        // Link to session if provided
        if (sessionId != null) {
            Session session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Session not found"));
            payment.setSession(session);
        }
        
        return paymentRepository.save(payment);
    }
    
    public Payment updatePaymentStatus(Long paymentId, Payment.PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }
    
    public List<Payment> getClientPayments(Long clientId) {
        return paymentRepository.findByClientId(clientId);
    }
    
    public List<Payment> getTherapistPayments(Long therapistId) {
        // Get all payments and filter by checking if the session's appointment belongs to this therapist
        return paymentRepository.findAll().stream()
            .filter(payment -> {
                if (payment.getSession() != null && payment.getSession().getAppointment() != null) {
                    return payment.getSession().getAppointment().getTherapist().getId().equals(therapistId);
                }
                return false;
            })
            .collect(java.util.stream.Collectors.toList());
    }
}
