package com.movie.review.app.movie_review_demo.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.movie.review.app.movie_review_demo.model.AuthResponse;
import com.movie.review.app.movie_review_demo.model.LoginRequest;
import com.movie.review.app.movie_review_demo.model.User;
import com.movie.review.app.movie_review_demo.model.UserDetailsResponse;
import com.movie.review.app.movie_review_demo.model.UserUpdateRequest;
import com.movie.review.app.movie_review_demo.service.UserService;

import jakarta.validation.Valid;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController

public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/users/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            // Check if username already exists
            if (userService.findByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Username already taken!");
            }

            if (userService.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Email already registered!");
            }

            User createdUser = userService.createUser(user);
            UserDetailsResponse userDetails = new UserDetailsResponse(
                    createdUser.getId(),
                    createdUser.getEmail(),
                    createdUser.getUsername());
            // Return token to frontend
            AuthResponse response = new AuthResponse(
                    createdUser.getAccessToken(),
                    "Account created successfully!",
                    userDetails);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {

            Optional<User> userOpt = userService.findByEmail(loginRequest.getEmail());

            if (userOpt.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email or password");
            }

            User user = userOpt.get();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(), // Spring Security uses username
                            loginRequest.getPassword()
                            )
                        );

            User updatedUser = userService.updateUserTokens(user);

            UserDetailsResponse userDetails = new UserDetailsResponse(
                    updatedUser.getId(),
                    updatedUser.getEmail(),
                    updatedUser.getUsername());

            AuthResponse response = new AuthResponse(
                    updatedUser.getAccessToken(),
                    "Login successful!",
                    userDetails);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            user.setPassword(null);
            user.setAccessToken(null);
            user.setRefreshToken(null);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest updated) {
        User user = userService.updateUser(id, updated);
        System.out.println("-------------user: "+user);
        if (user != null) {
            user.setPassword(null);
            user.getAccessToken();
            user.setRefreshToken(null);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}