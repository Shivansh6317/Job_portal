package com.example.auth.repository;

import com.example.auth.entity.ChatRoom;
import com.example.auth.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);
}
