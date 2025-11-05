package com.movie.review.app.movie_review_demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.review.app.movie_review_demo.model.Review;
import com.movie.review.app.movie_review_demo.model.User;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Long> {

    // Find all reviews for a specific movie
    List<Review> findByMovieId(Long movieId);

    // Find all reviews written by a specific user
    List<Review> findByUser(User user);
}
