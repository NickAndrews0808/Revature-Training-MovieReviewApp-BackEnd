package com.movie.review.app.movie_review_demo.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.movie.review.app.movie_review_demo.model.User;
import com.movie.review.app.movie_review_demo.model.UserUpdateRequest;
import com.movie.review.app.movie_review_demo.repo.UserRepo;
import com.movie.review.app.movie_review_demo.security.JwtUtil;

import jakarta.transaction.Transactional;


@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepo userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserTokens(User user) {
        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        user.setAccessToken(newAccessToken);
        user.setRefreshToken(newRefreshToken);
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User updateUser(Long id, UserUpdateRequest updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            updateUserTokens(user);
            // user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));//for password update
            return userRepository.save(user);
        }).orElse(null);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean validateTokenFromDatabase(String token) {
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            
            Optional<User> userOpt = findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return false; 
            }
            
            User user = userOpt.get();
            return token.equals(user.getAccessToken()) 
                   && jwtUtil.validateToken(token, username);
                   
        } catch (Exception e) {
            return false; 
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        User user = userOpt.get();
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.emptyList() // No roles for now (can add later)
        );
    }
}