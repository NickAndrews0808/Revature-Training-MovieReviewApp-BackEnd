package com.movie.review.app.movie_review_demo.controllers;

import com.movie.review.app.movie_review_demo.config.GoogleTokenVerifier;
import com.movie.review.app.movie_review_demo.config.JwtUtil;
import com.movie.review.app.movie_review_demo.model.User;
import com.movie.review.app.movie_review_demo.repo.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private final GoogleTokenVerifier googleTokenVerifier;
    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;

    public AuthController(GoogleTokenVerifier googleTokenVerifier, JwtUtil jwtUtil, UserRepo userRepo) {
        this.googleTokenVerifier = googleTokenVerifier;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
    }

    @PostMapping("/google/verify")
    public ResponseEntity<?> verifyGoogleAuth(@RequestBody Map<String, String> request) {
        try {
            String code = request.get("code");
            String redirectUri = request.get("redirectUri");

            if (code == null || redirectUri == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing code or redirectUri"));
            }

            // Verify Google authorization code and get user info
            Map<String, Object> googleUserInfo = googleTokenVerifier.verifyGoogleAuthCode(code, redirectUri);

            String email = (String) googleUserInfo.get("email");
            String name = (String) googleUserInfo.get("name");
            String picture = (String) googleUserInfo.get("picture");
			System.out.println("Google User Info: " + googleUserInfo);
			System.out.println("email User Info: " + email);
			System.out.println("name User Info: " + name);
			// System.out.println("Google User Info: " + googleUserInfo);

            if (email == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email not provided by Google"));
            }

            // Check if user exists, if not create new user
            User user = userRepo.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setUsername(name != null ? name.replaceAll("\\s+", "") : email.split("@")[0]);
                newUser.setPassword(""); // OAuth users don't have passwords
                newUser.setCreatedBy("google");
                newUser.setCreatedAt(LocalDateTime.now());
                newUser.setUpdatedAt(LocalDateTime.now());
                return userRepo.save(newUser);
            });

            // Generate JWT token
            String accessToken = jwtUtil.generateToken(email, name);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("user", Map.of(
                "email", email,
                "name", name,
                "picture", picture
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Google auth verification failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not authenticated"));
        }

        String email = (String) authentication.getPrincipal();
        Optional<User> user = userRepo.findByEmail(email);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", user.get().getEmail());
        userInfo.put("username", user.get().getUsername());
        userInfo.put("authenticated", true);

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // With JWT, logout is handled client-side by removing the token
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}