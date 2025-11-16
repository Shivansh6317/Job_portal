package com.example.auth.dto;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDTO {
    private Long id;
    private String name;
    private String description;
    private Instant createdAt;
    private List<MessageDTO> messages;
}
