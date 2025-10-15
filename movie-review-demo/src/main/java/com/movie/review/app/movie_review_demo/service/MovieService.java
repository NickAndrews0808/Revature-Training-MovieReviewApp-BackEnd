package com.movie.review.app.movie_review_demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.movie.review.app.movie_review_demo.model.Movie;
import com.movie.review.app.movie_review_demo.repo.MovieRepo;

@Service
public class MovieService {
    private final MovieRepo movieRepo;

    public MovieService(MovieRepo movieRepo){
        this.movieRepo = movieRepo;
    }
    public List<Movie> getAllMovies() {
        return movieRepo.findAll();
    }
    public Optional<Movie> getMovieById(Long id) {
        return movieRepo.findById(id);
    }

    public Movie createMovie(Movie movie) {
        return movieRepo.save(movie);
    }
    public Movie updateMovie(Long id, Movie updatedMovie) {
        return movieRepo.findById(id)
                .map(movie -> {
                    movie.setName(updatedMovie.getName());
                    movie.setYear(updatedMovie.getYear());
                    movie.setDirector(updatedMovie.getDirector());
                    movie.setGenre(updatedMovie.getGenre());
                    movie.setDescription(updatedMovie.getDescription());
                    movie.setAverageRating(updatedMovie.getAverageRating());
                    movie.setUpdatedBy(updatedMovie.getUpdatedBy());
                    return movieRepo.save(movie);
                })
                .orElseThrow(() -> new RuntimeException("Movie not found with id " + id));
    }
    public void deleteMovie(Long id) {
        movieRepo.deleteById(id);
    }
}
