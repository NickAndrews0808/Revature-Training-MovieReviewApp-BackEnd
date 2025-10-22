package com.movie.review.app.movie_review_demo.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.review.app.movie_review_demo.model.User;
import com.movie.review.app.movie_review_demo.repo.UserRepo;

@Service
public class AuthService {
	private final UserService userService;
	private final UserRepo userRepo;
	private final ObjectMapper mapper = new ObjectMapper();
	private final PasswordEncoder passwordEncoder;

	// Demo secret. In production, load from config and protect it.
	private final byte[] secret = "secure-random-secret-key-which-is-long".getBytes(StandardCharsets.UTF_8);

	public AuthService(UserService userService, UserRepo userRepo, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}

	public User register(User user) {
		// Hash password before saving
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userService.createUser(user);
	}
}
