package com.tms.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDto {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private String content;
    private String type;
    private LocalDateTime timestamp;
    private boolean isRead;
}
