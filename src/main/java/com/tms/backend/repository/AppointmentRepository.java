package com.tms.backend.repository;

import com.tms.backend.model.Appointment;
import com.tms.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByClient(User client);
    List<Appointment> findByTherapist(User therapist);
}
