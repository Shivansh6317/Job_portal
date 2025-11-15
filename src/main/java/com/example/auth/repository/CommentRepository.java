package com.example.auth.repository;

import com.example.auth.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import org.yaml.snakeyaml.events.CommentEvent;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long>
{
    List<Comment> findByPostId();


}
