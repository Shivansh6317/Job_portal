package com.example.auth.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.auth.dto.ChatRoomDTO;
import com.example.auth.dto.FileMessageRequest;
import com.example.auth.dto.MessageDTO;
import com.example.auth.entity.ChatRoom;
import com.example.auth.entity.Message;
import com.example.auth.entity.MessageStatus;
import com.example.auth.repository.ChatRoomRepository;
import com.example.auth.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final Cloudinary cloudinary;


    public ChatRoomDTO createRoom(String name, String description) {
        ChatRoom room = ChatRoom.builder()
                .name(name)
                .description(description)
                .createdAt(Instant.now())
                .build();

        return maptoDto(chatRoomRepository.save(room));
    }

    public List<ChatRoomDTO> getAllRooms() {
        return chatRoomRepository.findAll().stream()
                .map(this::maptoDto)
                .collect(Collectors.toList());
    }



    public ChatRoomDTO getRoom(Long id) {
        return chatRoomRepository.findById(id)
                .map(this::maptoDto)
                .orElse(null);
    }


    @Transactional
    public MessageDTO addTextMessage(Long roomId, MessageDTO dto) {

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found: " + roomId));

        Message msg = Message.builder()
                .senderId(dto.getSenderId())
                .content(dto.getContent())
                .sentAt(Instant.now())
                .hasFile(false)
                .status(MessageStatus.SENT)
                .chatRoom(room)
                .build();

        Message saved = messageRepository.save(msg);

        room.getMessages().add(saved);
        return toDto(saved);
    }

    @Transactional
    public MessageDTO addFileMessage(Long roomId, FileMessageRequest req) {

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found: " + roomId));

        try {
            Map upload = cloudinary.uploader().upload(
                    req.getFile().getBytes(),
                    ObjectUtils.asMap("resource_type", "auto")
            );

            String url = upload.get("secure_url").toString();
            String format = upload.getOrDefault("format", "bin").toString();
            String resourceType = upload.getOrDefault("resource_type", "raw").toString();

            String fileType = detectType(resourceType, format);

            Message msg = Message.builder()
                    .senderId(req.getSenderId())
                    .sentAt(Instant.now())
                    .hasFile(true)
                    .fileUrl(url)
                    .fileType(fileType)
                    .status(MessageStatus.SENT)
                    .chatRoom(room)
                    .build();

            Message saved = messageRepository.save(msg);

            room.getMessages().add(saved);
            return toDto(saved);

        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }


    @Transactional
    public MessageDTO markAsDelivered(Long messageId) {

        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));

        if (msg.getStatus() == MessageStatus.SENT) {
            msg.setStatus(MessageStatus.DELIVERED);
            messageRepository.save(msg);
        }

        return toDto(msg);
    }


    @Transactional
    public MessageDTO markAsRead(Long messageId) {

        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));

        msg.setStatus(MessageStatus.READ);
        messageRepository.save(msg);

        return toDto(msg);
    }

    public List<MessageDTO> getMessages(Long roomId) {

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found: " + roomId));

        return messageRepository.findByChatRoomOrderBySentAtAsc(room)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private String detectType(String resourceType, String format) {
        if ("image".equalsIgnoreCase(resourceType)) return "IMAGE";
        if ("video".equalsIgnoreCase(resourceType)) return "VIDEO";
        if ("raw".equalsIgnoreCase(resourceType) && format.equalsIgnoreCase("pdf")) return "PDF";
        if ("raw".equalsIgnoreCase(resourceType)) return "FILE";
        return "OTHER";
    }


    private MessageDTO toDto(Message m) {

        return MessageDTO.builder()
                .id(m.getId())
                .senderId(m.getSenderId())
                .content(m.getContent())
                .sentAt(m.getSentAt())
                .hasFile(m.isHasFile())
                .fileUrl(m.getFileUrl())
                .fileType(m.getFileType())
                .status(m.getStatus().name())
                .chatRoomId(
                        (m.getChatRoom() != null)
                                ? m.getChatRoom().getId()
                                : null
                )
                .build();
    }


    private ChatRoomDTO maptoDto(ChatRoom r) {

        List<MessageDTO> msgs = new ArrayList<>();

        if (r.getMessages() != null) {
            msgs = r.getMessages().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }

        return ChatRoomDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .description(r.getDescription())
                .createdAt(r.getCreatedAt())
                .messages(msgs)
                .build();
    }
}
