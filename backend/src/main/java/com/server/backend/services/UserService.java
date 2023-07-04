package com.server.backend.services;

import com.server.backend.entity.Group;
import com.server.backend.entity.User;
import com.server.backend.jwt.JwtService;
import com.server.backend.repository.GroupRepository;
import com.server.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final JwtService jwtService;

    public List<Group> getUserGroups(String token) {
        User user = getUserFromToken(token);
        return groupRepository.findAllByUserIn(user);
    }


    private User getUserFromToken(String token) {
        String username = jwtService.extractUsername(token.substring(7));
        return userRepository.findByUsername(username).orElseThrow();
    }
}
