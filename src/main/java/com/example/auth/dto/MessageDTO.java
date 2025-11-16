package com.example.auth.dto;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;
    private String senderId;
    private String content;
    private Instant sentAt;
    private boolean hasFile;
    private String fileUrl;
    private String fileType;
    private String status; // SENT / DELIVERED / READ
    private Long chatRoomId;
}
