package com.server.backend.websocket;

import com.server.backend.entity.User;
import com.server.backend.enums.MovieType;
import com.server.backend.services.HTTPService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GroupSession {

    private final Long groupId;
    private final Set<User> users = new HashSet<>();

    private String currentMovieTime = "0";
    private long lastTimeUpdate = 0L;
    private String movieState = "PAUSE";

    private MovieType movieType = null;

    private Map<String, String> resolutionStreamLinks = new HashMap<>();

    private String link;
    private String translation;
    private Integer season;
    private Integer episode;

    public GroupSession(Long groupId) {
        this.groupId = groupId;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public void removeUser(User user) {
        this.users.remove(user);
    }

    public Long getGroupId() {
        return groupId;
    }

    public Set<User> getUsers() {
        return users;
    }

    public String getCurrentMovieTime() {
        if (this.movieState.equals("PAUSE")) {
            return currentMovieTime;
        }

        float marginOfError = 0.5f;

        long elapsedTime = (System.currentTimeMillis() - this.lastTimeUpdate) / 1000;
        float currentTime = Float.parseFloat(this.currentMovieTime) + elapsedTime + marginOfError;
        return String.valueOf(currentTime);
    }

    public void setCurrentMovieTime(String time) {
        this.currentMovieTime = time;
        this.refreshLastTimeUpdate();
    }

    public String getMovieState() {
        return movieState;
    }

    public void setMovieState(String movieState) {
        this.movieState = movieState;
    }

    private void refreshLastTimeUpdate() {
        this.lastTimeUpdate = System.currentTimeMillis();
    }

    public Map<String, String> getResolutionStreamLinks() {
        return resolutionStreamLinks;
    }

    public void setResolutionStreamLinks(Map<String, String> resolutionStreamLinks) {
        this.resolutionStreamLinks = resolutionStreamLinks;
    }

    public void setMovie(String link, String translation) {
        this.season = null;
        this.episode = null;

        this.link = link;
        this.translation = translation;
        this.movieType = MovieType.MOVIE;
    }

    public void setSeries(String link, String translation, Integer season, Integer episode) {
        this.link = link;
        this.translation = translation;
        this.season = season;
        this.episode = episode;

        this.movieType = MovieType.SERIES;
    }

    public String getLink() {
        return link;
    }

    public String getTranslation() {
        return translation;
    }

    public Integer getSeason() {
        return season;
    }

    public Integer getEpisode() {
        return episode;
    }

    public MovieType getMovieType() {
        return movieType;
    }
}
