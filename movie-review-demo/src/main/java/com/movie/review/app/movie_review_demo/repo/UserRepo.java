package com.movie.review.app.movie_review_demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.review.app.movie_review_demo.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
   Optional<User> findByEmail(String email);

}
