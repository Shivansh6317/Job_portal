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


    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<PostResponse> createPost(@ModelAttribute com.example.auth.dto.PostCreateRequest request) throws IOException {
        PostResponse resp = postService.createPost(request.getFile(), request.getContent());
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


    @PostMapping("/{postId}/comment")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long postId,
                                                 @RequestBody CommentCreateRequest request) {
        return ResponseEntity.ok(postService.addComment(postId, request.getText()));
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
