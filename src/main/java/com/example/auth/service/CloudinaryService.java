package com.example.auth.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.auth.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folder) {

        try {
            String resourceType = getString(file);

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", resourceType
                    )
            );

            Object url = uploadResult.get("secure_url") != null
                    ? uploadResult.get("secure_url")
                    : uploadResult.get("url");

            if (url == null) {
                throw new CustomException("Failed to retrieve file URL", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return url.toString();

        } catch (IOException e) {
            throw new CustomException("File upload failed: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static String getString(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException("File is empty or missing", HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType();
        String resourceType = "auto";

        if (contentType != null) {
            if (contentType.startsWith("video/")) {
                resourceType = "video";
            } else if (contentType.startsWith("image/")) {
                resourceType = "image";
            } else if (contentType.equals("application/pdf")) {
                resourceType = "raw";
            }
        }
        return resourceType;
    }


}
