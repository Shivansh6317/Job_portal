package com.example.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Feed endpoint
        registry.addEndpoint("/ws-feed")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // Chat endpoint
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Client will subscribe to: /topic/*
        config.enableSimpleBroker("/topic");

        // Client will send message to: /app/*
        config.setApplicationDestinationPrefixes("/app");
    }
}
