package com.server.backend.controllers;

import com.server.backend.services.SeriesService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/series")
public class SeriesController {

    private final SeriesService seriesService;

    @GetMapping("/{groupId}/{resolution}/{season}/{episode}")
    public ResponseEntity<?> getSeriesStreamLink(@PathVariable Long groupId, @PathVariable String resolution,
                                                 @PathVariable Integer season, @PathVariable Integer episode) {
        Optional<String> streamLink = this.seriesService.getSeriesStreamLink(groupId, resolution, season, episode);
        if (streamLink.isPresent()) {
            return ResponseEntity.ok(streamLink);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to get video link");
        }
    }

}
