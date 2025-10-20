package com.movie.review.app.movie_review_demo.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.movie.review.app.movie_review_demo.model.User;
import com.movie.review.app.movie_review_demo.service.UserService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Getters
    @GetMapping("/users/{id}")
    public User getUserById(@RequestParam Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/users")
    public java.util.List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Setters
    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PostMapping("/user/update/{id}")
    public User updateUser(@RequestParam Long id, @Valid @RequestBody User updatedUser) {
        User existingUser = userService.getUserById(id);
        if (existingUser != null) {
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPassword(updatedUser.getPassword());
            existingUser.setUpdatedBy(updatedUser.getUpdatedBy());
            existingUser.setUpdatedAt(updatedUser.getUpdatedAt());
            return userService.updateUser(id, existingUser);
        }
        return null;
    }

    @PostMapping("/user/delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
