package com.server.backend.controllers;

import com.server.backend.entity.*;
import com.server.backend.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/groups")
    public ResponseEntity<List<Group>> getUserGroups(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.getUserGroups(token));
    }

    @GetMapping("/search/{username}")
    public ResponseEntity<Set<User>> findMatchingUsersByUsername(@PathVariable String username,
                                                                 @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(this.userService.findMatchingUsersByUsername(username, token));
    }

    @PostMapping("/friend_request")
    public void sendFriendRequest(@RequestBody User user, @RequestHeader("Authorization") String token) {
        this.userService.sendFriendRequest(user, token);
    }

}
