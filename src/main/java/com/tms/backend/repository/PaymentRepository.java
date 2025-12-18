package com.tms.backend.repository;

import com.tms.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.backend.model.Session;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByClientId(Long clientId);
    Optional<Payment> findBySession(Session session);
}
