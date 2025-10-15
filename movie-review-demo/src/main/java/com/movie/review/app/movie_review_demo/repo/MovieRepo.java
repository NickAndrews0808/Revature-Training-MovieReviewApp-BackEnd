package com.movie.review.app.movie_review_demo.repo;

import org.springframework.stereotype.Repository;

import com.movie.review.app.movie_review_demo.model.Movie;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface MovieRepo extends JpaRepository<Movie, Long>{
    
}
