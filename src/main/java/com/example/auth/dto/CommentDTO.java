
package com.example.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CommentDTO {
    private Long id;
    private String text;
    private String authorName;
    private Instant createdAt;
}
