package com.example.auth.controller;

import com.example.auth.dto.ChatRoomDTO;
import com.example.auth.dto.FileMessageRequest;
import com.example.auth.dto.MessageDTO;
import com.example.auth.entity.ChatRoom;
import com.example.auth.repository.ChatRoomRepository;
import com.example.auth.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;


    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomDTO> createRoom(@RequestBody ChatRoomDTO roomDTO) {
        ChatRoomDTO dto = chatService.createRoom(roomDTO.getName(), roomDTO.getDescription());
        return ResponseEntity.ok(dto);
    }

   // @GetMapping("/rooms")
//    public ResponseEntity<List<ChatRoomDTO>> getRooms() {
//        return ResponseEntity.ok(chatService.getAllRooms());
//    }
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDTO>> getRooms(){
        return ResponseEntity.ok(chatService.getAllRooms());

    }


    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoomDTO> getRoom(@PathVariable Long roomId) {
        ChatRoomDTO dto = chatService.getRoom(roomId);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }


    @PostMapping("/rooms/{roomId}/messages/text")
    public ResponseEntity<MessageDTO> sendText(
            @PathVariable Long roomId,
            @RequestBody MessageDTO messageDTO
    ) {
        return ResponseEntity.ok(chatService.addTextMessage(roomId, messageDTO));
    }


    @PostMapping(value = "/rooms/{roomId}/messages/file", consumes = "multipart/form-data")
    public ResponseEntity<MessageDTO> sendFile(
            @PathVariable Long roomId,
            @ModelAttribute FileMessageRequest request
    ) {
        return ResponseEntity.ok(chatService.addFileMessage(roomId, request));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getMessages(roomId));
    }


    @PatchMapping("/messages/{messageId}/delivered")
    public ResponseEntity<MessageDTO> markDelivered(@PathVariable Long messageId) {
        return ResponseEntity.ok(chatService.markAsDelivered(messageId));
    }

    @PatchMapping("/messages/{messageId}/read")
    public ResponseEntity<MessageDTO> markRead(@PathVariable Long messageId) {
        return ResponseEntity.ok(chatService.markAsRead(messageId));
    }
}
