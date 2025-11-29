

package com.example.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class PostResponse {
    private Long id;
    private String content;
    private String imageUrl;
    private String videoUrl;
    private AuthorDTO author;
    private Integer likeCount;
    private Integer shareCount;
    private Boolean likedByCurrentUser;
    private Instant createdAt;
    private List<CommentDTO> comments;
}
