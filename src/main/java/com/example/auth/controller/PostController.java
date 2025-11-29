package com.example.auth.controller;

import com.example.auth.dto.CommentCreateRequest;
import com.example.auth.dto.CommentDTO;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
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

            if (file != null) {
                log.info("CreatePost request received. fileName='{}', size={}, contentType={}",
                        file.getOriginalFilename(), file.getSize(), file.getContentType());
            } else {
                log.info("CreatePost request received without file.");
            }

            PostResponse resp = postService.createPost(file, content);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);

        } catch (MaxUploadSizeExceededException mex) {
            log.warn("Upload rejected - file too large: {}", mex.getMessage());
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(Map.of(
                            "error", "File too large",
                            "details", mex.getMessage(),
                            "exception", mex.getClass().getName()
                    ));
        } catch (MultipartException mmex) {

            log.warn("Multipart parsing error: {}", mmex.getMessage(), mmex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Malformed multipart request",
                            "details", mmex.getMessage(),
                            "exception", mmex.getClass().getName()
                    ));
        } catch (Exception e) {

            log.error("Create post failed: {} - {}", e.getClass().getName(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Failed to create post",
                            "details", e.getMessage(),
                            "exception", e.getClass().getName()
                    ));
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
                    .body(Map.of("error", "Failed to load feed", "details", e.getMessage(), "exception", e.getClass().getName()));
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        try {
            PostResponse resp = postService.getPost(postId);
            return ResponseEntity.ok(resp);
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", rnfe.getMessage(), "exception", rnfe.getClass().getName()));
        } catch (Exception e) {
            log.error("Failed to load post {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to load post", "details", e.getMessage(), "exception", e.getClass().getName()));
        }
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long postId) {
        try {
            int newCount = postService.toggleLike(postId);
            return ResponseEntity.ok(Map.of("likeCount", newCount));
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", rnfe.getMessage(), "exception", rnfe.getClass().getName()));
        } catch (Exception e) {
            log.error("Failed to toggle like for post {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to toggle like", "details", e.getMessage(), "exception", e.getClass().getName()));
        }
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> addComment(@PathVariable Long postId,
                                        @Valid @RequestBody CommentCreateRequest request) {
        try {
            CommentDTO dto = postService.addComment(postId, request.getText());
            return ResponseEntity.ok(dto);
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", rnfe.getMessage(), "exception", rnfe.getClass().getName()));
        } catch (Exception e) {
            log.error("Failed to add comment to post {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add comment", "details", e.getMessage(), "exception", e.getClass().getName()));
        }
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId) {
        try {
            List<CommentDTO> comments = postService.getComments(postId);
            return ResponseEntity.ok(comments);
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", rnfe.getMessage(), "exception", rnfe.getClass().getName()));
        } catch (Exception e) {
            log.error("Failed to get comments for post {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get comments", "details", e.getMessage(), "exception", e.getClass().getName()));
        }
    }

    @PostMapping("/{postId}/share")
    public ResponseEntity<?> share(@PathVariable Long postId) {
        try {
            ShareResponse resp = postService.sharePost(postId);
            return ResponseEntity.ok(resp);
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", rnfe.getMessage(), "exception", rnfe.getClass().getName()));
        } catch (Exception e) {
            log.error("Failed to share post {}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to share post", "details", e.getMessage(), "exception", e.getClass().getName()));
        }
    }
}
