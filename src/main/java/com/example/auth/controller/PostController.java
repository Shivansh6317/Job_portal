package com.example.auth.controller;


import com.example.auth.dto.*;
import com.example.auth.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<PostResponse> createPost(@RequestParam(value = "file", required = false) MultipartFile file,
                                                   @RequestParam(value = "content", required = false) String content) throws IOException {
        PostResponse resp = postService.createPost(file, content);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getFeed() {
        return ResponseEntity.ok(postService.getFeed());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long postId) {
        int newCount = postService.toggleLike(postId);
        return ResponseEntity.ok().body(java.util.Map.of("likeCount", newCount));
    }

    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getLikeCount(postId));
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long postId, @RequestParam("text") String text) {
        return ResponseEntity.ok(postService.addComment(postId, text));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getComments(postId));
    }

    @PostMapping("/{postId}/share")
    public ResponseEntity<ShareResponse> share(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.sharePost(postId));
    }
}
