package com.tms.backend.repository;

import com.tms.backend.model.Appointment;
import com.tms.backend.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByAppointment_ClientId(Long clientId);
    List<Session> findByAppointment_TherapistId(Long therapistId);
    Optional<Session> findByAppointment(Appointment appointment);
}
