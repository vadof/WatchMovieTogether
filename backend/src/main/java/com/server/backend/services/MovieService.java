package com.server.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.backend.entity.*;
import com.server.backend.enums.MovieType;
import com.server.backend.repository.*;
import com.server.backend.websocket.WebSocketService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;
    private final ResolutionRepository resolutionRepository;
    private final TranslationRepository translationRepository;
    private final GroupRepository groupRepository;

    private final MovieSettingsRepository movieSettingsRepository;

    private final HTTPService httpService;
    private final SeriesService seriesService;
    private final ChatService chatService;
    private final WebSocketService webSocketService;

    private final String REZKA_API_URL = "http://localhost:5000/api";

    private Optional<MovieType> getMovieType(String link) {
        try {
            String requestBody = String.format("{\"url\":\"%s\"}", link);
            String response = this.httpService.sendPostRequest(requestBody, REZKA_API_URL + "/movie/type");
            if (response.contains("movie")) {
                return Optional.of(MovieType.MOVIE);
            } else if (response.contains("tv_series")) {
                return Optional.of(MovieType.SERIES);
            }

        } catch (Exception e) {
            log.error("Error getting movie type with link: {}", link, e);
        }

        return Optional.empty();
    }

    public Optional<?> getMovieOrSeries(String link) {
        Optional<MovieType> optionalMovieType = this.getMovieType(link);
        if (optionalMovieType.isPresent()) {
            MovieType movieType = optionalMovieType.get();
            if (movieType.equals(MovieType.MOVIE)) {
                return this.getMovie(link);
            } else {
                return seriesService.getSeries(link);
            }
        } else {
            return Optional.empty();
        }
    }

    private Optional<Movie> getMovie(String link) {
        Optional<Movie> optionalMovie = movieRepository.findByLink(link);
        if (optionalMovie.isEmpty()) {
            optionalMovie = sendMovieRequest(link);
            optionalMovie.ifPresent(this::saveMovie);
        } else {
            Movie movie = optionalMovie.get();
            movie.addSearch();
            movieRepository.save(movie);
        }
        return optionalMovie;
    }

    private Optional<Movie> sendMovieRequest(String movieLink) {
        String requestBody = String.format("{\"url\":\"%s\"}", movieLink);
        String movieJsonString = httpService.sendPostRequest(requestBody, REZKA_API_URL + "/movie");
        return parseMovieFromString(movieJsonString);
    }

    @Transactional
    private void saveMovie(Movie movie) {
        try {
            for (Translation translation : movie.getTranslations()) {
                List<Resolution> resolutions = new ArrayList<>();
                for (Resolution r : translation.getResolutions()) {
                    resolutions.add(resolutionRepository.findByValue(r.getValue()));
                }
                translation.setResolutions(resolutions);
                translationRepository.save(translation);
            }
            movieRepository.save(movie);
            log.info("Movie saved to database: {}", movie.getLink());
        } catch (Exception e) {
            log.error("Error occurred while saving the movie: {}", movie.getLink(), e);
        }
    }

    private Optional<Movie> parseMovieFromString(String s) {
        try {
            String elementToCut = "{\"movie\":";
            s = s.substring(elementToCut.length());

            ObjectMapper objectMapper = new ObjectMapper();
            return Optional.of(objectMapper.readValue(s, Movie.class));
        } catch (Exception e) {
            log.error("Error while parsing a movie object " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<String> getMovieStreamLink(Long groupId, String resolution) {
        try {
            Group group = this.groupRepository.findById(groupId).orElseThrow();
            MovieSettings movieSettings = group.getGroupSettings().getMovieSettings();

            String movieUrl = movieSettings.getSelectedMovie().getLink();
            Translation selectedTranslation = movieSettings.getSelectedTranslation();

            boolean resolutionExists = selectedTranslation.getResolutions().stream()
                    .anyMatch(r -> r.getValue().equals(resolution));
            if (resolutionExists) {
                String requestBody = String.format("{\"url\":\"%s\",\"translation\":\"%s\",\"resolution\":\"%s\"}",
                        movieUrl, selectedTranslation.getName(), resolution);

                String streamLink = this.httpService.sendPostRequest(requestBody, REZKA_API_URL + "/movie/link");
                streamLink = streamLink.substring(streamLink.indexOf("http"), streamLink.lastIndexOf(".mp4") + 4);

                return Optional.of(streamLink);
            }
        } catch (Exception e) {
            log.error("Error getting movie stream link {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Movie> updateMovieInfo(Movie movie) {
        try {
            movie = movieRepository.findByLink(movie.getLink()).orElseThrow();
            Movie updatedMovie = this.sendMovieRequest(movie.getLink()).orElseThrow();

            if (!movie.equals(updatedMovie)) {
                movie.setName(updatedMovie.getName());

                for (Translation translation : updatedMovie.getTranslations()) {
                    List<Resolution> resolutions = new ArrayList<>();
                    for (Resolution resolution : translation.getResolutions()) {
                        resolutions.add(resolutionRepository.findByValue(resolution.getValue()));
                    }
                    translation.setResolutions(resolutions);
                }

                this.translationRepository.saveAll(updatedMovie.getTranslations());

                this.changeSelectedTranslationForAllGroups(movie, updatedMovie.getTranslations());

                this.translationRepository.deleteAll(movie.getTranslations());
                movie.setTranslations(updatedMovie.getTranslations());

                this.movieRepository.save(movie);

                log.info("Updated movie {}", movie.getLink());
            }

            return Optional.of(updatedMovie);
        } catch (Exception e) {
            log.error("Error updating movie with link {}, Error: {}", movie.getLink(), e.getMessage());
            return Optional.empty();
        }
    }

    private void changeSelectedTranslationForAllGroups(Movie movie, List<Translation> newTranslations) {
        List<MovieSettings> movieSettingsList = this.movieSettingsRepository.findAllBySelectedMovie(movie);

        for (MovieSettings ms : movieSettingsList) {
            Optional<Translation> sameTranslation = newTranslations
                    .stream()
                    .filter(t -> t.equals(ms.getSelectedTranslation()))
                    .findAny();

            if (sameTranslation.isPresent()) {
                ms.setSelectedTranslation(sameTranslation.get());
            } else {
                ms.setSelectedTranslation(newTranslations.get(0));

                Long groupId = this.groupRepository.findByMovieSettings(ms).getId();
                this.webSocketService.sendObjectByWebsocket("/group/" + groupId + "/movie", ms);
                this.chatService.sendMovieUpdateMessage(groupId);
            }
        }

        this.movieSettingsRepository.saveAll(movieSettingsList);
    }
}
