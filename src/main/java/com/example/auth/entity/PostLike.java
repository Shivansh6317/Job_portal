package com.example.auth.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="post_likes", uniqueConstraints =
        {
                @UniqueConstraint(columnNames = {"post_id","user_id"})
        })

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="post_id")
    private Post post;



    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

}
