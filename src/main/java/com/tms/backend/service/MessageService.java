package com.tms.backend.service;

import com.tms.backend.dto.MessageDto;
import com.tms.backend.model.Message;
import com.tms.backend.model.User;
import com.tms.backend.repository.MessageRepository;
import com.tms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public MessageDto sendMessage(Long senderId, Long receiverId, String content, String type) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setRead(false);
        try {
            message.setType(com.tms.backend.model.MessageType.valueOf(type));
        } catch (IllegalArgumentException | NullPointerException e) {
            message.setType(com.tms.backend.model.MessageType.TEXT);
        }
        
        Message saved = messageRepository.save(message);
        
        // Notify receiver
        String notificationMessage = type.equals("MISSED_CALL") 
            ? "Missed call from " + sender.getFullName()
            : "New message from " + sender.getFullName();
            
        notificationService.createNotification(receiverId, notificationMessage, "MESSAGE");
        
        return convertToDto(saved);
    }
    
    public List<MessageDto> getConversation(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Message> messages1 = messageRepository.findBySenderAndReceiver(user1, user2);
        List<Message> messages2 = messageRepository.findBySenderAndReceiver(user2, user1);
        
        messages1.addAll(messages2);
        
        return messages1.stream()
                .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<MessageDto> getUnreadMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return messageRepository.findByReceiverAndIsReadFalse(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        message.setRead(true);
        messageRepository.save(message);
    }
    
    private MessageDto convertToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getFullName());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setReceiverName(message.getReceiver().getFullName());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        dto.setRead(message.isRead());
        dto.setType(message.getType() != null ? message.getType().name() : "TEXT");
        return dto;
    }
}
