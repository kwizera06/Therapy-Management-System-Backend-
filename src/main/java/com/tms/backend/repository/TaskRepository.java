package com.tms.backend.repository;

import com.tms.backend.model.Task;
import com.tms.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByClient(User client);
    List<Task> findByTherapist(User therapist);
    List<Task> findByGoalId(Long goalId);
}
