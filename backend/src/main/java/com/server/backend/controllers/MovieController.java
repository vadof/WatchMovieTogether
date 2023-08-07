package com.server.backend.controllers;

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
        Optional<?> movieOrSeries = movieService.getMovieOrSeries(link);
        if (movieOrSeries.isPresent()) {
            return ResponseEntity.ok(movieOrSeries.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid URL provided");
        }
    }

    @GetMapping("/{groupId}/{resolution}")
    public ResponseEntity<?> getMovieStreamLink(@PathVariable Long groupId, @PathVariable String resolution) {
        Optional<String> streamLink = this.movieService.getMovieStreamLink(groupId, resolution);
        if (streamLink.isPresent()) {
            return ResponseEntity.ok(streamLink);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to get video link");
        }
    }

}
