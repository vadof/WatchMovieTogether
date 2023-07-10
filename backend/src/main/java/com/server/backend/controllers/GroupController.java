package com.server.backend.controllers;

import com.server.backend.entity.Chat;
import com.server.backend.entity.Group;
import com.server.backend.requests.MovieSelectionRequest;
import com.server.backend.services.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/group")
@AllArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestHeader("Authorization") String token,
                                             @RequestBody String name) {
        return ResponseEntity.ok(groupService.createGroup(name, token));
    }

    @PostMapping("/movie")
    public ResponseEntity<String> setUpMovieForGroup(@RequestBody MovieSelectionRequest movieSelectionRequest) {
        if (groupService.setUpMovieForGroup(movieSelectionRequest)) {
            return ResponseEntity.ok("Movie customized for the group");
        }
        return ResponseEntity.ok("Something went wrong!");
    }

    @GetMapping("/chat/{id}")
    public ResponseEntity<Chat> getGroupChat(@PathVariable(value = "id") Long groupId,
                                             @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(groupService.getGroupChat(groupId, token).orElseThrow());
    }
}
