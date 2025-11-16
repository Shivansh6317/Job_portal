package com.example.auth.dto;

import java.time.LocalDateTime;
import lombok.*;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private Long id;
    private String content;
    private String imageUrl;
    private String videoUrl;
    private AuthorDTO author;
    private int likeCount;
    private int shareCount;
    private boolean likedByCurrentUser;
    private LocalDateTime createdAt;
    private List<CommentDTO> comments;

}
