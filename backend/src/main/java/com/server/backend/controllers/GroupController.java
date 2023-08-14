package com.server.backend.controllers;

import com.server.backend.entity.*;
import com.server.backend.services.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/groups")
@AllArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestHeader("Authorization") String token,
                                             @RequestBody String name) {
        return ResponseEntity.ok(this.groupService.createGroup(name, token));
    }

    @PostMapping("/{groupId}/movie")
    public void setUpMovieForGroup(@PathVariable Long groupId, @RequestBody MovieSettings movieSettings) {
        this.groupService.setUpMovieForGroup(groupId, movieSettings);
    }

    @PostMapping("/{groupId}/series")
    public void setUpSeriesForGroup(@PathVariable Long groupId, @RequestBody SeriesSettings seriesSettings) {
        this.groupService.setUpSeriesForGroup(groupId, seriesSettings);
    }

    @GetMapping("/chat/{id}")
    public ResponseEntity<Chat> getGroupChat(@PathVariable(value = "id") Long groupId,
                                             @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(this.groupService.getGroupChat(groupId, token).orElseThrow());
    }

    @PostMapping("/{groupId}/users")
    public void addUserToGroup(@PathVariable Long groupId, @RequestBody User user) {
        this.groupService.addUserToGroup(groupId, user);
    }

    @PutMapping("/{groupId}/movie/translation")
    public void changeMovieTranslationForGroup(@PathVariable Long groupId, @RequestBody Translation translation) {
        this.groupService.changeSelectedMovieTranslation(groupId, translation);
    }

    @PutMapping("/{groupId}/series/episode")
    public void changeSeriesEpisode(@PathVariable Long groupId,
                                    @RequestBody SeriesSettings seriesSettings) {
        this.groupService.changeEpisodeInSeries(groupId,
                seriesSettings.getSelectedSeason(), seriesSettings.getSelectedEpisode());
    }
}
