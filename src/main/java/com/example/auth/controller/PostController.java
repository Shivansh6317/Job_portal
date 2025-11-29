package com.example.auth.controller;

import com.example.auth.dto.*;
import com.example.auth.exception.ResourceNotFoundException;
import com.example.auth.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.io.IOException;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Validated
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(@ModelAttribute PostCreateRequest request) {
        try {
            PostResponse resp = postService.createPost(request.getFile(), request.getContent());
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IOException e) {
            log.error("File upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file", "details", e.getMessage()));
        } catch (Exception e) {
            log.error("Create post failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
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
            log.error("Failed toggle like", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to toggle like"));
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
            log.error("Failed add comment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add comment"));
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
            log.error("Failed get comments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get comments"));
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
            log.error("Failed to share post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to share post"));
        }
    }
}
