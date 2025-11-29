package com.example.auth.controller;

import com.example.auth.dto.CommentCreateRequest;
import com.example.auth.dto.CommentDTO;
import com.example.auth.dto.PostCreateRequest;
import com.example.auth.dto.PostResponse;
import com.example.auth.dto.ShareResponse;
import com.example.auth.exception.ResourceNotFoundException;
import com.example.auth.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class PostController {

    private final PostService postService;


    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @RequestPart(value = "content", required = false) String content,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            PostResponse resp = postService.createPost(file, content);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e) {

            log.error("Create post failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create post", "details", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getFeed() {
        try {
            List<PostResponse> feed = postService.getFeed();
            return ResponseEntity.ok(feed);
        } catch (Exception e) {
            log.error("Failed to load feed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to load feed", "details", e.getMessage()));
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        try {
            PostResponse resp = postService.getPost(postId);
            return ResponseEntity.ok(resp);
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", rnfe.getMessage()));
        } catch (Exception e) {
            log.error("Failed to load post {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to load post", "details", e.getMessage()));
        }
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long postId) {
        try {
            int newCount = postService.toggleLike(postId);
            return ResponseEntity.ok(Map.of("likeCount", newCount));
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", rnfe.getMessage()));
        } catch (Exception e) {
            log.error("Failed to toggle like for post {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to toggle like", "details", e.getMessage()));
        }
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> addComment(@PathVariable Long postId,
                                        @Valid @RequestBody CommentCreateRequest request) {
        try {
            CommentDTO dto = postService.addComment(postId, request.getText());
            return ResponseEntity.ok(dto);
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", rnfe.getMessage()));
        } catch (Exception e) {
            log.error("Failed to add comment to post {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add comment", "details", e.getMessage()));
        }
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId) {
        try {
            List<CommentDTO> comments = postService.getComments(postId);
            return ResponseEntity.ok(comments);
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", rnfe.getMessage()));
        } catch (Exception e) {
            log.error("Failed to get comments for post {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get comments", "details", e.getMessage()));
        }
    }

    @PostMapping("/{postId}/share")
    public ResponseEntity<?> share(@PathVariable Long postId) {
        try {
            ShareResponse resp = postService.sharePost(postId);
            return ResponseEntity.ok(resp);
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", rnfe.getMessage()));
        } catch (Exception e) {
            log.error("Failed to share post {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to share post", "details", e.getMessage()));
        }
    }
}
