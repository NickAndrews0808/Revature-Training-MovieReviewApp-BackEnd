package com.movie.review.app.movie_review_demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.movie.review.app.movie_review_demo.model.Review;
import com.movie.review.app.movie_review_demo.model.User;
import com.movie.review.app.movie_review_demo.repo.ReviewRepo;
import com.movie.review.app.movie_review_demo.repo.UserRepo;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepo reviewRepository;

    @Autowired
    private UserRepo userRepo;

    // Create a new review
    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    // Get all reviews
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // Get review by ID
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    // Get reviews by Movie ID
    public List<Review> getReviewsByMovieId(Long movieId) {
        return reviewRepository.findByMovieId(movieId);
    }

    // Get reviews by User ID
    public List<Review> getReviewsByUserId(Long userId) {
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        return reviewRepository.findByUser(userOpt.get());
    }

    // Update an existing review
    public Review updateReview(Long id, Review updatedReview) {
        return reviewRepository.findById(id)
                .map(review -> {
                    review.setRating(updatedReview.getRating());
                    review.setReview(updatedReview.getReviewText());
                    review.setUpdatedBy(updatedReview.getUpdatedBy());
                    return reviewRepository.save(review);
                })
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));
    }

    // Delete a review
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}

