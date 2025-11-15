package com.example.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShareResponse {
    private Long postId;
    private String shareLink;
    private int shareCount;
    private String message;
}
