package com.movie.review.app.movie_review_demo.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.movie.review.app.movie_review_demo.model.Review;
import com.movie.review.app.movie_review_demo.model.User;
import com.movie.review.app.movie_review_demo.repo.UserRepo;
import com.movie.review.app.movie_review_demo.service.ReviewService;
import com.movie.review.app.movie_review_demo.repo.ReviewRepo;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private UserRepo userRepo;

    // Get all reviews
    @GetMapping("/movie-list")
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    // Get review by ID
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update review
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody Review updatedReview) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Fetch existing review
            Review review = reviewService.getReviewById(id)
                    .orElseThrow(() -> new RuntimeException("Review not found"));

            // Only allow updating if it's the same user
            if (!review.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("You can only edit your own reviews");
            }

            review.setRating(updatedReview.getRating());
            review.setReview(updatedReview.getReviewText());
            review.setUpdatedBy(user.getUsername());

            Review savedReview = reviewService.createReview(review);
            return ResponseEntity.ok(savedReview);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // Delete review
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Review review = reviewService.getReviewById(id)
                    .orElseThrow(() -> new RuntimeException("Review not found"));

            if (!review.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("You can only delete your own reviews");
            }

            reviewService.deleteReview(id);
            return ResponseEntity.ok("Review deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // Create a review
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody Review review) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            review.setUsername(user.getUsername());
            review.setUser(user);
            review.setCreatedBy(user.getUsername());
            review.setCreatedOn(LocalDateTime.now());

            Review saved = reviewService.createReview(review);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating review: " + e.getMessage());
        }
    }

    // Get all reviews for a specific movie
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsForMovie(@PathVariable Long movieId) {
        List<Review> reviews = reviewService.getReviewsByMovieId(movieId);

        // Map each Review to ReviewDTO
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(r -> new ReviewDTO(
                        r.getId(),
                        r.getRating(),
                        r.getReview(), // map the field "review" -> reviewText in DTO
                        r.getCreatedOn(),
                        r.getUsername()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(reviewDTOs);
    }
}
