package com.example.auth.service;

import com.example.auth.dto.*;
import com.example.auth.entity.*;
import com.example.auth.exception.ResourceNotFoundException;
import com.example.auth.repository.*;
import com.example.auth.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final CloudinaryService cloudinaryService;
    private final NotificationService notificationService;
    private final AuthUtil authUtil;

    @Value("${app.frontend.url:}")
    private String frontendBaseUrl;

    @Transactional
    public PostResponse createPost(MultipartFile file, String content) throws IOException {
        User user = authUtil.getCurrentUser(); // may throw if unauthenticated

        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            // may throw IOException
            fileUrl = cloudinaryService.uploadFile(file, "posts");
        }

        Post post = Post.builder()
                .user(user)
                .content(content != null ? content.trim() : null)
                .imageUrl(fileUrl)
                .likeCount(0)
                .shareCount(0)
                .build();

        Post saved = postRepository.save(post);

        try {
            notificationService.sendPostUpdate(saved, "NEW_POST");
        } catch (Exception ex) {
            log.warn("Notification failed for new post id={}", saved.getId(), ex);
        }

        return mapToDto(saved, user);
    }


    public List<PostResponse> getFeed() {
        try {
            User currentUser = authUtil.getCurrentUserOrNull();
            List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
            return posts.stream().map(p -> mapToDto(p, currentUser)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching feed", e);
            return List.of();
        }
    }


    public PostResponse getPost(Long postId) {
        try {
            User currentUser = authUtil.getCurrentUserOrNull();
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
            return mapToDto(post, currentUser);
        } catch (ResourceNotFoundException rnfe) {
            throw rnfe;
        } catch (Exception e) {
            log.error("Unexpected error getting post {}", postId, e);
            throw new RuntimeException("Failed to load post");
        }
    }

    @Transactional
    public int toggleLike(Long postId) {
        User user = authUtil.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        return postLikeRepository.findByPostAndUser(post, user)
                .map(existing -> {
                    postLikeRepository.delete(existing);
                    int newCount = Math.max(0, post.getLikeCount() - 1);
                    post.setLikeCount(newCount);
                    postRepository.save(post);
                    try { notificationService.sendPostUpdate(post, "LIKE_UPDATE"); } catch (Exception ignored) {}
                    return post.getLikeCount();
                })
                .orElseGet(() -> {
                    PostLike pl = PostLike.builder().post(post).user(user).build();
                    postLikeRepository.save(pl);
                    post.setLikeCount(post.getLikeCount() + 1);
                    postRepository.save(post);
                    try { notificationService.sendPostUpdate(post, "LIKE_UPDATE"); } catch (Exception ignored) {}
                    return post.getLikeCount();
                });
    }

    public long getLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        return postLikeRepository.countByPost(post);
    }


    @Transactional
    public CommentDTO addComment(Long postId, String text) {
        User user = authUtil.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .text(text)
                .build();

        Comment saved = commentRepository.save(comment);

        try { notificationService.sendPostUpdate(post, "COMMENT_ADDED"); } catch (Exception ignored) {}

        return CommentDTO.builder()
                .id(saved.getId())
                .text(saved.getText())
                .authorName(user != null ? user.getName() : "Unknown")
                .createdAt(Instant.from(saved.getCreatedAt()))
                .build();
    }


    public List<CommentDTO> getComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        return commentRepository.findByPostId(postId).stream()
                .map(c -> CommentDTO.builder()
                        .id(c.getId())
                        .text(c.getText())
                        .authorName(c.getUser() != null ? c.getUser().getName() : "Unknown")
                        .createdAt(Instant.from(c.getCreatedAt()))
                        .build())
                .collect(Collectors.toList());
    }


    @Transactional
    public ShareResponse sharePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        post.setShareCount(post.getShareCount() + 1);
        postRepository.save(post);

        try { notificationService.sendPostUpdate(post, "SHARE_UPDATE"); } catch (Exception ignored) {}

        String link = generateShareLink(postId);
        return ShareResponse.builder()
                .postId(postId)
                .shareLink(link)
                .shareCount(post.getShareCount())
                .message("Post shared")
                .build();
    }

    private String generateShareLink(Long postId) {
        String base = (frontendBaseUrl != null && !frontendBaseUrl.isBlank()) ? frontendBaseUrl : "";
        return base + "/post/" + postId;
    }

    private PostResponse mapToDto(Post post, User currentUser) {
        boolean liked = currentUser != null && postLikeRepository.existsByPostAndUser(post, currentUser);

        List<CommentDTO> comments = post.getComments() != null
                ? post.getComments().stream()
                .map(c -> CommentDTO.builder()
                        .id(c.getId())
                        .text(c.getText())
                        .authorName(c.getUser() != null ? c.getUser().getName() : "Unknown")
                        .createdAt(Instant.from(c.getCreatedAt()))
                        .build())
                .collect(Collectors.toList())
                : List.of();

        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .videoUrl(post.getVideoUrl())
                .author(AuthorDTO.builder()
                        .id(post.getUser() != null ? post.getUser().getId() : null)
                        .name(post.getUser() != null ? post.getUser().getName() : "Unknown")
                        .build())
                .likeCount(post.getLikeCount())
                .shareCount(post.getShareCount())
                .likedByCurrentUser(liked)
                .createdAt(Instant.from(post.getCreatedAt()))
                .comments(comments)
                .build();
    }
}
