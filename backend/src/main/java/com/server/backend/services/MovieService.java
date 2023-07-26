package com.server.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.backend.entity.Group;
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
    private final GroupRepository groupRepository;

    private final String MOVIE_API_URL = "http://localhost:5000/api/movie";
    private static final Logger LOG = LoggerFactory.getLogger(MovieService.class);

    public Optional<Movie> getMovie(String link) {
        Optional<Movie> optionalMovie = movieRepository.findByLink(link);
        if (optionalMovie.isEmpty()) {
            String movieJsonString = sendMovieRequest(link);
            optionalMovie = parseMovieFromString(movieJsonString);
            optionalMovie.ifPresent(this::saveMovie);
        } else {
            Movie movie = optionalMovie.get();
            movie.addSearch();
            movieRepository.save(movie);
        }
        return optionalMovie;
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

    private String sendMovieRequest(String link) {
        try {
            String requestBody = String.format("{\"url\":\"%s\"}", link);
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

    public Optional<String> getVideoLinkByResolution(Long groupId, String resolution) {
        try {
            Group group = this.groupRepository.findById(groupId).orElseThrow();

            String movieUrl = group.getGroupSettings().getSelectedMovie().getLink();
            Translation groupSelectedTranslation = group.getGroupSettings().getSelectedTranslation();
            String resolutionValue = groupSelectedTranslation.getResolutions().stream()
                    .filter(r -> r.getValue().equals(resolution))
                    .findFirst().orElseThrow().getValue();

            String requestBody = String.format("{\"url\":\"%s\",\"translation\":\"%s\",\"resolution\":\"%s\"}",
                    movieUrl, groupSelectedTranslation.getName(), resolutionValue);

            String videoLink = sendHttpRequest(requestBody, new URL(MOVIE_API_URL + "/link"));
            videoLink = videoLink.substring(videoLink.indexOf("http"), videoLink.lastIndexOf(".mp4") + 4);

            return Optional.of(videoLink);
        } catch (Exception e) {
            return Optional.empty();
        }

    }
}
