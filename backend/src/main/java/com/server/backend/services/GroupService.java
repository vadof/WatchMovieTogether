package com.server.backend.services;

import com.server.backend.entity.Group;
import com.server.backend.entity.User;
import com.server.backend.jwt.JwtService;
import com.server.backend.repository.GroupRepository;
import com.server.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public Group createGroup(String name, String token) {
        User user = jwtService.getUserFromBearerToken(token).orElseThrow();

        Group group = new Group();
        group.setName(name);
        group.getUsers().add(user);
        group.setAdmin(user.getUsername());
        group.getUsers().add(user);
        groupRepository.save(group);

        user.getGroups().add(group);
        userRepository.save(user);

        return group;
    }

}
