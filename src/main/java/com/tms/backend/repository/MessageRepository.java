package com.tms.backend.repository;

import com.tms.backend.model.Message;
import com.tms.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiver(User sender, User receiver);
    List<Message> findByReceiverAndIsReadFalse(User receiver);
    List<Message> findBySender(User sender);
    List<Message> findByReceiver(User receiver);
}
