package com.example.auth.controller;

import com.example.auth.dto.MessageDTO;
import com.example.auth.dto.FileMessageRequest;
import com.example.auth.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, MessageDTO message) {
        // persist and broadcast
        MessageDTO saved = chatService.addTextMessage(roomId, message);
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId, saved);
    }


    @MessageMapping("/typing/{roomId}")
    public void typing(@DestinationVariable Long roomId, Map<String, String> payload) {
        messagingTemplate.convertAndSend("/topic/typing/" + roomId, payload);
    }

    @MessageMapping("/typing-stop/{roomId}")
    public void typingStop(@DestinationVariable Long roomId, Map<String, String> payload) {
        messagingTemplate.convertAndSend("/topic/typing-stop/" + roomId, payload);
    }


    @MessageMapping("/delivered/{messageId}")
    public void delivered(@DestinationVariable Long messageId) {
        MessageDTO updated = chatService.markAsDelivered(messageId);

        messagingTemplate.convertAndSend("/topic/delivered/" + updated.getChatRoomId(), updated);
    }


    @MessageMapping("/read/{messageId}")
    public void read(@DestinationVariable Long messageId) {
        MessageDTO updated = chatService.markAsRead(messageId);
        messagingTemplate.convertAndSend("/topic/read/" + updated.getChatRoomId(), updated);
    }
}
