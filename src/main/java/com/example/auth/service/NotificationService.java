package com.example.auth.service;

import com.example.auth.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendPostUpdate(Post post, String eventType) {
        Map<String, Object> update = new HashMap<>();
        update.put("postId", post.getId());
        update.put("eventType", eventType);
        update.put("likeCount", post.getLikeCount());
        update.put("shareCount", post.getShareCount());
        update.put("content", post.getContent());
        update.put("imageUrl", post.getImageUrl());
        messagingTemplate.convertAndSend("/topic/posts", update);
    }
}