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

    public List<User> getUserFriends(String token) {
        return this.jwtService.getUserFromBearerToken(token).orElseThrow().getFriends();
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

    public List<User> getFriendRequests(String token) {
        return jwtService.getUserFromBearerToken(token).orElseThrow().getFriendRequests();
    }

    public void acceptFriendRequest(User addedUser, String userTokenWhoAccepted) {
        User userWhoAccepted = jwtService.getUserFromBearerToken(userTokenWhoAccepted).orElseThrow();
        addedUser = userRepository.findByUsername(addedUser.getUsername()).orElseThrow();

        userWhoAccepted.getFriendRequests().remove(addedUser);

        userWhoAccepted.getFriends().add(addedUser);
        addedUser.getFriends().add(userWhoAccepted);

        userRepository.save(userWhoAccepted);
        userRepository.save(addedUser);
    }

    public void denyFriendRequest(User deniedUser, String userTokenWhoDenied) {
        User userWhoDenied = jwtService.getUserFromBearerToken(userTokenWhoDenied).orElseThrow();
        deniedUser = userRepository.findByUsername(deniedUser.getUsername()).orElseThrow();

        userWhoDenied.getFriendRequests().remove(deniedUser);

        userRepository.save(userWhoDenied);
    }

    public void removeFriend(String username, String token) {
        User user = jwtService.getUserFromBearerToken(token).orElseThrow();
        User userToRemove = userRepository.findByUsername(username).orElseThrow();

        user.getFriends().remove(userToRemove);
        userToRemove.getFriends().remove(user);

        userRepository.save(user);
        userRepository.save(userToRemove);
    }
}
