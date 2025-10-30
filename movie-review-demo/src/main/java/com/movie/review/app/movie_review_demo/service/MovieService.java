package com.movie.review.app.movie_review_demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.movie.review.app.movie_review_demo.model.Movie;
import com.movie.review.app.movie_review_demo.model.User;
import com.movie.review.app.movie_review_demo.repo.MovieRepo;
import com.movie.review.app.movie_review_demo.repo.UserRepo;

@Service
public class MovieService {
    private final MovieRepo movieRepo;
    private final UserRepo userRepo;

    public MovieService(MovieRepo movieRepo, UserRepo userRepo){
        this.movieRepo = movieRepo;
        this.userRepo = userRepo;
    }
    public List<Movie> getAllMovies() {
        return movieRepo.findAll();
    }
    public Optional<Movie> getMovieById(Long id) {
        return movieRepo.findById(id);
    }

    public Movie createMovie(Movie movie) {
        // Assign a default user (replace with current authenticated user later)
        User defaultUser = userRepo.findById(1L).orElseThrow(() -> 
            new RuntimeException("Default user not found"));
        movie.setCreatedBy(defaultUser);
        movie.setUpdatedBy(defaultUser); // optional
        return movieRepo.save(movie);
    }
    private String convertToEmbedUrl(String url) {
        if (url == null || url.isBlank()) return null;
        if (url.contains("youtu.be/")) {
            String videoId = url.split("youtu.be/")[1].split("\\?")[0];
            return "https://www.youtube.com/embed/" + videoId;
        } else if (url.contains("watch?v=")) {
            String videoId = url.split("watch\\?v=")[1].split("&")[0];
            return "https://www.youtube.com/embed/" + videoId;
        } else if (url.contains("embed/")) {
            return url; // already correct format
        } else {
            return url; // fallback
        }
    }

    public Movie updateMovie(Long id, Movie updatedMovie) {
        Movie existing = movieRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Movie not found"));
        
        existing.setName(updatedMovie.getName());
        existing.setYear(updatedMovie.getYear());
        existing.setDirector(updatedMovie.getDirector());
        existing.setGenre(updatedMovie.getGenre());
        existing.setDescription(updatedMovie.getDescription());
        existing.setAverageRating(updatedMovie.getAverageRating());
        existing.setImageUrl(updatedMovie.getImageUrl());
        existing.setYtUrl(convertToEmbedUrl(updatedMovie.getYtUrl()));

        // Assign updatedBy user
        User defaultUser = userRepo.findById(1L).orElseThrow(() -> 
            new RuntimeException("Default user not found"));
        existing.setUpdatedBy(defaultUser);

        return movieRepo.save(existing);
    }

    public void deleteMovie(Long id) {
        movieRepo.deleteById(id);
    }
}
