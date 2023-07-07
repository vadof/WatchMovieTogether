package com.server.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.backend.entity.Movie;
import com.server.backend.entity.Resolution;
import com.server.backend.entity.Translation;
import com.server.backend.repository.GroupRepository;
import com.server.backend.repository.MovieRepository;
import com.server.backend.repository.ResolutionRepository;
import com.server.backend.repository.TranslationRepository;
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
    private final ResolutionRepository resolutionRepository;
    private final TranslationRepository translationRepository;

    private final String MOVIE_API_URL = "http://localhost:5000/api/movie";
    private static final Logger LOG = LoggerFactory.getLogger(MovieService.class);

    public Optional<Movie> getMovie(String link) {
        Optional<Movie> optionalMovie = movieRepository.findByLink(link);
        if (optionalMovie.isEmpty()) {
            String movieJsonString = sendMovieRequest(link);
            optionalMovie = parseMovieFromString(movieJsonString);
            optionalMovie.ifPresent(this::saveMovie);
        }
        return optionalMovie;
    }

    public String getVideoLink(String movieLink, Translation translation, Resolution resolution) {
        try {
            String requestBody = "{\"url\": \"" + movieLink
                                    + "\",\"translation\": \"" + translation.getName()
                                    + "\", \"resolution\": \"" + resolution.getValue()
                                    + "\"}";

            return sendHttpRequest(requestBody, new URL(MOVIE_API_URL + "/stream"));
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    @Transactional
    private void saveMovie(Movie movie) {
        try {
            for (Translation translation : movie.getTranslations()) {
                List<Resolution> resolutions = new ArrayList<>();
                for (Resolution resolution : translation.getResolutions()) {
                    resolutions.add(resolutionRepository.findByValue(resolution.getValue()));
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

    private String sendMovieRequest(String link) {
        try {
            String requestBody = "{\"url\": \"" + link + "\"}";
            String responseBody = sendHttpRequest(requestBody, new URL(MOVIE_API_URL));
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
            LOG.error("Error while parsing an object " + e.getMessage());
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
            LOG.error("Error in parsing " + requestBody);
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
}
