package com.tudor.demo.controller;

import com.tudor.demo.model.PasswordResetToken;
import com.tudor.demo.model.User;
import com.tudor.demo.repository.UserRepository;
import com.tudor.demo.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/password-reset")
public class PasswordResetTokenController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/request")
    public Map<String, String> requestPasswordReset(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        System.out.println("Password reset request received for: " + email);

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }
        User user = optionalUser.get();
        PasswordResetToken token = passwordResetService.createPasswordResetToken(user);

        System.out.println("Password reset token generated: " + token.getToken());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset link has been generated.");
        response.put("token", token.getToken());
        return response;
    }

    @PostMapping("/reset")
    public Map<String, String> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");

        boolean success = passwordResetService.resetPassword(token, newPassword);

        Map<String, String> response = new HashMap<>();
        if (success) {
            response.put("message", "Password reset successful.");
        } else {
            response.put("message", "Invalid or expired token.");
        }
        return response;
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmReset(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        System.out.println("Token: " + token);
        System.out.println("New Password: " + newPassword);


        boolean result = passwordResetService.resetPassword(token, newPassword);
        if (result) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Password has been reset."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid or expired token."));
        }
    }
}
