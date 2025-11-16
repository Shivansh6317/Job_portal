package com.example.auth.dto;

import lombok.*;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime createdAt;

}
