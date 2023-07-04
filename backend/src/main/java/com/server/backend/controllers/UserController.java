package com.server.backend.controllers;

import com.server.backend.entity.*;
import com.server.backend.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/groups")
    public ResponseEntity<List<Group>> getUserGroups(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.getUserGroups(token));
    }

}
