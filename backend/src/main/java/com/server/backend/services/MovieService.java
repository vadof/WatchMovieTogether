package com.server.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.backend.entity.*;
import com.server.backend.enums.MovieType;
import com.server.backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;
    private final ResolutionRepository resolutionRepository;
    private final TranslationRepository translationRepository;
    private final SeriesTranslationRepository seriesTranslationRepository;
    private final SeasonRepository seasonRepository;
    private final GroupRepository groupRepository;

    private final String REZKA_API_URL = "http://localhost:5000/api";
    private static final Logger LOG = LoggerFactory.getLogger(MovieService.class);

    private Optional<MovieType> getMovieType(String link) {
        try {
            String requestBody = String.format("{\"url\":\"%s\"}", link);
            String response = this.sendHttpRequest(requestBody, new URL(REZKA_API_URL + "/movie/type"));
            if (response.contains("movie")) {
                return Optional.of(MovieType.MOVIE);
            } else if (response.contains("tv_series")) {
                return Optional.of(MovieType.SERIES);
            }

        } catch (Exception e) {
            LOG.error("Error getting movie type with link: {}", link, e);
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
                return this.getSeries(link);
            }
        } else {
            return Optional.empty();
        }
    }

    private Optional<Movie> getMovie(String link) {
        Optional<Movie> optionalMovie = movieRepository.findByLink(link);
        if (optionalMovie.isEmpty()) {
            String movieJsonString = sendHTTPRequest(link, REZKA_API_URL + "/movie");
            optionalMovie = parseMovieFromString(movieJsonString);
            optionalMovie.ifPresent(this::saveMovie);
        } else {
            Movie movie = optionalMovie.get();
            movie.addSearch();
            movieRepository.save(movie);
        }
        return optionalMovie;
    }

    private Optional<Series> getSeries(String link) {
        if (link.contains("#")) {
            link = link.substring(0, link.indexOf("#"));
        }
        Optional<Series> optionalSeries = seriesRepository.findByLink(link);
        if (optionalSeries.isEmpty()) {
            String seriesJsonString = sendHTTPRequest(link, REZKA_API_URL + "/series");
            optionalSeries = parseSeriesFromString(seriesJsonString);
            optionalSeries.ifPresent(this::saveSeries);
        } else {
            Series series = optionalSeries.get();
            series.addSearch();
            seriesRepository.save(series);
        }
        return optionalSeries;
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
            LOG.info("Movie saved to database: {}", movie.getLink());
        } catch (Exception e) {
            LOG.error("Error occurred while saving the movie: {}", movie.getLink(), e);
        }
    }

    @Transactional
    private void saveSeries(Series series) {
        try {
            for (SeriesTranslation seriesTranslation : series.getSeriesTranslations()) {
                List<Resolution> resolutions = new ArrayList<>();
                for (Resolution r : seriesTranslation.getResolutions()) {
                    resolutions.add(resolutionRepository.findByValue(r.getValue()));
                }
                seriesTranslation.setResolutions(resolutions);
                seasonRepository.saveAll(seriesTranslation.getSeasons());

                seriesTranslationRepository.save(seriesTranslation);
            }

            seriesRepository.save(series);
            LOG.info("Series saved to database: {}", series.getLink());
        } catch (Exception e) {
            LOG.error("Error occurred while saving the series: {}", series.getLink(), e);
        }
    }

    private String sendHTTPRequest(String link, String url) {
        try {
            String requestBody = String.format("{\"url\":\"%s\"}", link);
            String responseBody = this.sendHttpRequest(requestBody, new URL(url));
            return decodeUnicodeEscapeSequences(responseBody);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return e.getMessage();
        }
    }

    private String decodeUnicodeEscapeSequences(String input) {
        Pattern pattern = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher matcher = pattern.matcher(input);

        StringBuilder output = new StringBuilder();
        while (matcher.find()) {
            String unicodeSequence = matcher.group(1);
            int unicodeValue = Integer.parseInt(unicodeSequence, 16);
            String replacement = Character.toString((char) unicodeValue);
            matcher.appendReplacement(output, replacement);
        }

        matcher.appendTail(output);
        return output.toString();
    }

    private Optional<Movie> parseMovieFromString(String s) {
        try {
            String elementToCut = "{\"movie\":";
            s = s.substring(elementToCut.length());

            ObjectMapper objectMapper = new ObjectMapper();
            return Optional.of(objectMapper.readValue(s, Movie.class));
        } catch (Exception e) {
            LOG.error("Error while parsing a movie object " + e.getMessage());
        }
        return Optional.empty();
    }

    private Optional<Series> parseSeriesFromString(String s) {
        try {
            String elementToCut = "{\"series\":";
            s = s.substring(elementToCut.length());

            ObjectMapper objectMapper = new ObjectMapper();
            return Optional.of(objectMapper.readValue(s, Series.class));
        } catch (Exception e) {
            LOG.error("Error while parsing a series object " + e.getMessage());
        }
        return Optional.empty();
    }

    private String sendHttpRequest(String requestBody, URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        connection.setDoOutput(true);
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(requestBody.getBytes());
        outputStream.flush();
        outputStream.close();

        int responseCode = connection.getResponseCode();

        BufferedReader reader;
        if (responseCode >= 200 && responseCode < 300) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            LOG.error("Error sending HTTP request  " + requestBody);
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }

        StringBuilder responseBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBody.append(line);
        }
        reader.close();

        return responseBody.toString();
    }

    public Optional<String> getMovieLinkByResolution(Long groupId, String resolution) {
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

                String videoLink = sendHttpRequest(requestBody, new URL(REZKA_API_URL + "/movie/link"));
                videoLink = videoLink.substring(videoLink.indexOf("http"), videoLink.lastIndexOf(".mp4") + 4);

                return Optional.of(videoLink);
            }
        } catch (Exception e) {
            LOG.error("Error getting movie link by resolution {}", e.getMessage());
        }
        return Optional.empty();
    }
}
