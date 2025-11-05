package com.movie.review.app.movie_review_demo.controllers;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewDTO {

    private Long id;
    private Integer rating;
    private String reviewText;
    private LocalDateTime createdAt;
    private String username;

    public ReviewDTO(Long id, int rating, String reviewText, LocalDateTime createdAt, String username) {
        this.id = id;
        this.rating = rating;
        this.reviewText = reviewText;
        this.createdAt = createdAt;
        this.username = username;
    }

    public Long getId() { return id; }
    public int getRating() { return rating; }
    public String getReviewText() { return reviewText; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getUsername() { return username; }
}
