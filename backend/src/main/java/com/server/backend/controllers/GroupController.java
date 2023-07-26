package com.server.backend.controllers;

import com.server.backend.entity.Chat;
import com.server.backend.entity.Group;
import com.server.backend.entity.Translation;
import com.server.backend.entity.User;
import com.server.backend.requests.MovieSelectionRequest;
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
        return ResponseEntity.ok(groupService.createGroup(name, token));
    }

    @PostMapping("/movie")
    public void setUpMovieForGroup(@RequestBody MovieSelectionRequest movieSelectionRequest) {
        groupService.setUpMovieForGroup(movieSelectionRequest);
    }

    @GetMapping("/chat/{id}")
    public ResponseEntity<Chat> getGroupChat(@PathVariable(value = "id") Long groupId,
                                             @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(groupService.getGroupChat(groupId, token).orElseThrow());
    }

    @PostMapping("/{groupId}/users")
    public void addUserToGroup(@PathVariable Long groupId, @RequestBody User user) {
        this.groupService.addUserToGroup(groupId, user);
    }

    @PutMapping("/{groupId}/translation")
    public void changeMovieTranslationForGroup(@PathVariable Long groupId, @RequestBody Translation translation) {
        this.groupService.changeMovieTranslation(groupId, translation);
    }
}
