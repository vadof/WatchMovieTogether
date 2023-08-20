package com.server.backend.websocket;

import com.server.backend.entity.User;

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

    // TODO map with link to don't get it always
    private Map<String, String> resolutionStreamLinks = new HashMap<>();

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
}
