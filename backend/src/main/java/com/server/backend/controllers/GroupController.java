package com.server.backend.controllers;

import com.server.backend.entity.Group;
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

}
