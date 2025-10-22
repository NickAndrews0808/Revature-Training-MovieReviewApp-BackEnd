package com.movie.review.app.movie_review_demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.review.app.movie_review_demo.model.User;
import com.movie.review.app.movie_review_demo.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	// Register a new user (delegates to UserService via AuthService)
	@PostMapping("/register")
	public ResponseEntity<User> register(@Valid @RequestBody User user) {
		User created = authService.register(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}
}
