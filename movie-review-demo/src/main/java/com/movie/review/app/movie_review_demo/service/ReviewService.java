package com.movie.review.app.movie_review_demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.movie.review.app.movie_review_demo.model.Review;
import com.movie.review.app.movie_review_demo.repo.ReviewRepo;

@Service
public class ReviewService {
        @Autowired
    private ReviewRepo reviewRepository;

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
        return reviewRepository.findByUserId(userId);
    }

    // Update an existing review
    public Review updateReview(Long id, Review updatedReview) {
        return reviewRepository.findById(id)
                .map(review -> {
                    review.setRating(updatedReview.getRating());
                    review.setReview(updatedReview.getReview());
                    review.setUpdateBy(updatedReview.getUpdateBy());
                    return reviewRepository.save(review);
                })
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));
    }

    // Delete a review
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}