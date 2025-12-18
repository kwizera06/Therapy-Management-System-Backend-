package com.tms.backend.controller;

import com.tms.backend.dto.MessageResponse;
import com.tms.backend.model.Payment;
import com.tms.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @PostMapping
    @PreAuthorize("hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<Payment> createPayment(
            @RequestParam Long clientId,
            @RequestParam(required = false) Long sessionId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String transactionId) {
        Payment payment = paymentService.createPayment(clientId, sessionId, amount, currency, transactionId);
        return ResponseEntity.ok(payment);
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status);
        Payment payment = paymentService.updatePaymentStatus(id, paymentStatus);
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getClientPayments(@PathVariable Long clientId) {
        return ResponseEntity.ok(paymentService.getClientPayments(clientId));
    }
    
    @GetMapping("/therapist/{therapistId}")
    @PreAuthorize("hasRole('THERAPIST') or hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getTherapistPayments(@PathVariable Long therapistId) {
        return ResponseEntity.ok(paymentService.getTherapistPayments(therapistId));
    }
}
