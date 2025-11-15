package com.example.auth.service;

import com.example.auth.dto.*;
import com.example.auth.entity.*;

import com.example.auth.repository.*;
import com.example.auth.service.CloudinaryService;
import com.example.auth.service.NotificationService;
import com.example.auth.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final CloudinaryService cloudinaryService;
    private final NotificationService notificationService;

    @Value("${app.frontend.url}")
    private String frontendBaseUrl;

    // Create post
    @Transactional
    public PostResponse createPost(MultipartFile file, String content) throws IOException {
        User user = AuthUtil.getCurrentUser(); // may throw if unauth
        String fileUrl = cloudinaryService.uploadFile(file);

        Post p = Post.builder()
                .user(user)
                .content(content)
                .imageUrl(fileUrl)
                .build();

        Post saved = postRepository.save(p);
        notificationService.sendPostUpdate(saved, "NEW_POST");
        return mapToDto(saved, user);
    }

    // Feed
    public List<PostResponse> getFeed() {
        User currentUser = AuthUtil.getCurrentUserOrNull();
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(post -> mapToDto(post, currentUser))
                .collect(Collectors.toList());
    }

    // Get single post
    public PostResponse getPost(Long postId) {
        User currentUser = AuthUtil.getCurrentUserOrNull();
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        return mapToDto(post, currentUser);
    }

    // Toggle like/unlike
    @Transactional
    public int toggleLike(Long postId) {
        User user = AuthUtil.getCurrentUser();
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        return postLikeRepository.findByPostAndUser(post, user)
                .map(existing -> {
                    postLikeRepository.delete(existing);
                    post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                    postRepository.save(post);
                    notificationService.sendPostUpdate(post, "LIKE_UPDATE");
                    return post.getLikeCount();
                })
                .orElseGet(() -> {
                    PostLike pl = PostLike.builder().post(post).user(user).build();
                    postLikeRepository.save(pl);
                    post.setLikeCount(post.getLikeCount() + 1);
                    postRepository.save(post);
                    notificationService.sendPostUpdate(post, "LIKE_UPDATE");
                    return post.getLikeCount();
                });
    }

    public long getLikeCount(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        return postLikeRepository.countByPost(post);
    }

    // Comments
    @Transactional
    public CommentDTO addComment(Long postId, String text) {
        User user = AuthUtil.getCurrentUser();
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder().post(post).user(user).text(text).build();
        Comment saved = commentRepository.save(comment);
        notificationService.sendPostUpdate(post, "COMMENT_ADDED");
        return CommentDTO.builder().id(saved.getId()).text(saved.getText()).authorName(user.getName()).createdAt(saved.getCreatedAt()).build();
    }

    public List<CommentDTO> getComments(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(c -> CommentDTO.builder().id(c.getId()).text(c.getText()).authorName(c.getUser().getName()).createdAt(c.getCreatedAt()).build())
                .collect(Collectors.toList());
    }

    // Share
    @Transactional
    public ShareResponse sharePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        post.setShareCount(post.getShareCount() + 1);
        postRepository.save(post);
        notificationService.sendPostUpdate(post, "SHARE_UPDATE");
        String link = generateShareLink(postId);
        return ShareResponse.builder().postId(postId).shareLink(link).shareCount(post.getShareCount()).message("Post shared").build();
    }

    private String generateShareLink(Long postId) {
        return frontendBaseUrl + "/post/" + postId;
    }

    // Mapping helper
    private PostResponse mapToDto(Post post, User currentUser) {
        boolean liked = currentUser != null && postLikeRepository.existsByPostAndUser(post, currentUser);
        List<CommentDTO> comments = post.getComments().stream()
                .map(c -> CommentDTO.builder().id(c.getId()).text(c.getText()).authorName(c.getUser() != null ? c.getUser().getName() : "Unknown").createdAt(c.getCreatedAt()).build())
                .collect(Collectors.toList());
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .videoUrl(post.getVideoUrl())
                .author(AuthorDTO.builder().id(post.getUser() != null ? post.getUser().getId() : null).name(post.getUser() != null ? post.getUser().getName() : "Unknown").build())
                .likeCount(post.getLikeCount())
                .shareCount(post.getShareCount())
                .likedByCurrentUser(liked)
                .createdAt(post.getCreatedAt())
                .comments(comments)
                .build();
    }
}
