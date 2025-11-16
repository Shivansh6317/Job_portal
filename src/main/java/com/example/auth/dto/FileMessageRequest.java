package com.example.auth.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileMessageRequest {
    private String senderId;
    private MultipartFile file;
}
