package com.example.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Instant sentAt;

    private boolean hasFile;
    private String fileUrl;
    private String fileType;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ChatRoom chatRoom;
}
