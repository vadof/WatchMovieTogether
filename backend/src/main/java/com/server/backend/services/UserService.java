package com.server.backend.services;

import com.server.backend.entity.Group;
import com.server.backend.entity.User;
import com.server.backend.jwt.JwtService;
import com.server.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public List<Group> getUserGroups(String token) {
        User user = jwtService.getUserFromBearerToken(token).orElseThrow();
        return user.getGroups().stream().toList();
    }

    public Set<User> findMatchingUsersByUsername(String username, String token) {
        User exceptUser = jwtService.getUserFromBearerToken(token).orElseThrow();

        List<User> similarUsers = userRepository.findUsersByUsernameMatches(username);
        similarUsers.remove(exceptUser);
        exceptUser.getFriends().forEach(similarUsers::remove);

        return new HashSet<>(similarUsers);
    }

    public void sendFriendRequest(User to, String token) {
        User from = jwtService.getUserFromBearerToken(token).orElseThrow();
        to = userRepository.findByUsername(to.getUsername()).orElseThrow();

        if (to.getFriendRequests().stream()
                .noneMatch(user -> user.getUsername().equals(from.getUsername()))) {
            to.getFriendRequests().add(from);
            userRepository.save(to);
        }
    }
}
