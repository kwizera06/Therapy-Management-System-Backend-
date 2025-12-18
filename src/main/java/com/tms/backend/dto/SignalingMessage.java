package com.tms.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignalingMessage {
    private String type; // "offer", "answer", "ice-candidate", "end-call"
    private String sdp;
    private Object candidate;
    private Long senderId;
    private Long receiverId;
}
