package com.server.backend.services;

import com.server.backend.entity.Group;
import com.server.backend.entity.User;
import com.server.backend.jwt.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final JwtService jwtService;

    public List<Group> getUserGroups(String token) {
        User user = jwtService.getUserFromBearerToken(token).orElseThrow();
        return user.getGroups().stream().toList();
    }
}
