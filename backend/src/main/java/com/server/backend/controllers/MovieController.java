package com.server.backend.controllers;

import com.server.backend.entity.Movie;
import com.server.backend.services.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/movie")
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<?> getMovieFromLink(@RequestBody String link) {
        Optional<Movie> movie = movieService.getMovie(link);
        if (movie.isPresent()) {
            return ResponseEntity.ok(movie.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid URL provided");
        }
    }

}
