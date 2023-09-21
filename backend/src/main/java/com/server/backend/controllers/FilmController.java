package com.server.backend.controllers;

import com.server.backend.objects.LatestMovieRelease;
import com.server.backend.services.FilmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/last-releases")
    public List<LatestMovieRelease> getLastReleases() {
        return filmService.getLatestMovieReleases();
    }

}
