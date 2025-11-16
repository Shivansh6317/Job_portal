package com.example.auth.service;

import com.example.auth.entity.Otp;
import com.example.auth.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public String generateOtp(String email) {
        String otpCode = String.format("%06d", new Random().nextInt(999999));


        otpRepository.deleteByEmail(email);

        Otp otp = Otp.builder()
                .email(email)
                .otp(otpCode)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .attempts(0)
                .build();
        otpRepository.save(otp);

        sendOtpEmail(email, otpCode);

        return otpCode;
    }

    private void sendOtpEmail(String toEmail, String otpCode) {
        String url = "https://api.brevo.com/v3/smtp/email";

        Map<String, Object> body = new HashMap<>();
        body.put("sender", Map.of("name", senderName, "email", senderEmail));
        body.put("to", List.of(Map.of("email", toEmail)));
        body.put("subject", "Your OTP Code");
        body.put("htmlContent", "<p>Hello,</p><p>Your verification code is: <strong>" +
                otpCode + "</strong></p><p>This code will expire in 5 minutes.</p><br><p>-JOB Platform</p>");

        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", brevoApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

    @Transactional
    public boolean verifyOtp(String email, String otpCode) {
        return otpRepository.findByEmail(email)
                .filter(o -> o.getExpiryTime().isAfter(LocalDateTime.now()))
                .filter(o -> o.getOtp().equals(otpCode))
                .map(o -> {
                    otpRepository.delete(o);
                    return true;
                }).orElse(false);
    }
}
