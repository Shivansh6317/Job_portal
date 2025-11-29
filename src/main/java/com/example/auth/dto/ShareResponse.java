
package com.example.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShareResponse {
    private Long postId;
    private String shareLink;
    private Integer shareCount;
    private String message;
}
