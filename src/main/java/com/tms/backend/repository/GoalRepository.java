package com.tms.backend.repository;

import com.tms.backend.model.Goal;
import com.tms.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByClient(User client);
    List<Goal> findByTherapist(User therapist);
}
