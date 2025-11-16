package com.example.auth.util;

import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;



//accessing curently authenticated user

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final UserRepository userRepository;

  //it will return authenticated user from db, else will give error
    public User getCurrentUser() {
        User user = getCurrentUserOrNull();
        if (user == null) {
            throw new RuntimeException("User not authenticated or token invalid");
        }
        return user;
    }



   // gives authenticated user as object
    public User getCurrentUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        String email = null;

        // Case 1: if principal is a UserDetails object
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername(); // username = email in your JWT setup
        }

        // Case 2: if principal directly stores the email string
        else if (principal instanceof String str) {
            // Sometimes Spring stores email or username as String
            if (!str.equalsIgnoreCase("anonymousUser")) {
                email = str;
            }
        }

        if (email == null) {
            return null;
        }

        // Fetch user details from DB
        return userRepository.findByEmail(email).orElse(null);
    }

// returns currently authenticated user details object
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof String str && !str.equalsIgnoreCase("anonymousUser")) {
            return str;
        }
        return null;
    }
}
