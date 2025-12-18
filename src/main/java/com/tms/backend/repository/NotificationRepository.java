package com.tms.backend.repository;

import com.tms.backend.model.Notification;
import com.tms.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByTimestampDesc(User user);
    List<Notification> findByUserAndIsReadFalse(User user);
}
