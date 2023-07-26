package com.server.backend.services;

import com.server.backend.entity.*;
import com.server.backend.jwt.JwtService;
import com.server.backend.repository.*;
import com.server.backend.requests.MovieSelectionRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupSettingsRepository groupSettingsRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ChatRepository chatRepository;
    private final ChatService chatService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(GroupService.class);

    @Transactional
    public Group createGroup(String name, String token) {
        User user = jwtService.getUserFromBearerToken(token).orElseThrow();

        Group group = new Group();
        group.setName(name);
        group.getUsers().add(user);
        group.setAdmin(user);

        Chat chat = new Chat();
        chatRepository.save(chat);
        group.setChat(chat);

        GroupSettings groupSettings = new GroupSettings();
        groupSettings.getUsersWithPrivileges().add(user);
        groupSettingsRepository.save(groupSettings);

        group.setGroupSettings(groupSettings);
        groupRepository.save(group);

        user.getGroups().add(group);
        userRepository.save(user);

        chatService.addSystemMessageToGroupChat(group.getId(),
                chatService.generateGroupCreateMessage(user.getUsername()));

        return group;
    }

    @Transactional
    public boolean setUpMovieForGroup(MovieSelectionRequest msr) {
        try {
            Movie movie = movieRepository.findByLink(msr.getMovie().getLink()).get();
            Group group = groupRepository.findById(msr.getGroupId()).get();
            Translation translation = movie.getTranslations()
                    .stream()
                    .filter(t -> t.equals(msr.getSelectedTranslation()))
                    .findFirst().get();

            GroupSettings groupSettings = group.getGroupSettings();
            groupSettings.setSelectedMovie(movie);
            groupSettings.setSelectedTranslation(translation);

            groupSettingsRepository.save(groupSettings);

            String movieChangeMessage = chatService.generateMovieChangeMessage(
                    movie.getName(), translation.getName());
            chatService.addSystemMessageToGroupChat(group.getId(), movieChangeMessage);

            return true;
        } catch (Exception e) {
            LOG.error("Failed to set up movie for group " + e.getMessage());
            return false;
        }
    }

    public Optional<Chat> getGroupChat(Long groupId, String token) {
        User user = jwtService.getUserFromBearerToken(token).orElseThrow();
        Group group = groupRepository.findById(groupId).orElseThrow();
        if (group.getUsers().stream().anyMatch(u -> u.equals(user))) {
            return Optional.of(group.getChat());
        }
        return Optional.empty();
    }

    public void addUserToGroup(Long groupId, User user) {
        Group group = groupRepository.findById(groupId).orElseThrow();
        user = userRepository.findByUsername(user.getUsername()).orElseThrow();

        if (!group.getUsers().contains(user)) {
            group.getUsers().add(user);
            groupRepository.save(group);

            user.getGroups().add(group);
            userRepository.save(user);

            this.simpMessagingTemplate.convertAndSend("/group/" + groupId + "/user/add", user);

            chatService.addSystemMessageToGroupChat(group.getId(),
                    chatService.generateUserJoinMessage(user.getUsername()));
        }
    }

    @Transactional
    public User removeUserFromGroup(Long groupId, User removedUser, String whoRemovedUsername) {
        Group group = groupRepository.findById(groupId).orElseThrow();
        removedUser = userRepository.findByUsername(removedUser.getUsername()).orElseThrow();

        group.getUsers().remove(removedUser);

        group.getGroupSettings().getUsersWithPrivileges().remove(removedUser);
        groupSettingsRepository.save(group.getGroupSettings());

        removedUser.getGroups().remove(group);
        userRepository.save(removedUser);

        if (group.getUsers().isEmpty()) {
            groupRepository.delete(group);
        } else {
            if (group.getAdmin().equals(removedUser)) {
                User newAdmin = group.getUsers().stream().findFirst().get();
                group.getGroupSettings().getUsersWithPrivileges().add(newAdmin);
                groupSettingsRepository.save(group.getGroupSettings());
                group.setAdmin(newAdmin);
            }

            if (removedUser.getUsername().equals(whoRemovedUsername)) {
                chatService.addSystemMessageToGroupChat(groupId,
                        chatService.generateUserLeaveMessage(removedUser.getUsername()));
            } else {
                chatService.addSystemMessageToGroupChat(groupId,
                        chatService.generateUserKickedMessage(removedUser.getUsername(), whoRemovedUsername));
            }

            groupRepository.save(group);
        }

        return removedUser;
    }

    public void changeMovieTranslation(Long groupId, Translation translation) {
        Group group = groupRepository.findById(groupId).orElseThrow();
        Movie movie = group.getGroupSettings().getSelectedMovie();

        if (!translation.equals(group.getGroupSettings().getSelectedTranslation())) {
            Translation translation1 = movie.getTranslations()
                    .stream()
                    .filter(t -> t.getName().equals(translation.getName()))
                    .findFirst().orElseThrow();

            group.getGroupSettings().setSelectedTranslation(translation1);
            groupRepository.save(group);

            chatService.addSystemMessageToGroupChat(group.getId(),
                    chatService.generateTranslationChangeMessage(translation.getName()));
        }
    }

    public Set<User> changeUserPrivileges(User user, Long groupId, String whoChangedUsername) {
        user = userRepository.findByUsername(user.getUsername()).orElseThrow();
        Group group = groupRepository.findById(groupId).orElseThrow();
        User whoChanged = userRepository.findByUsername(whoChangedUsername).orElseThrow();

        Set<User> usersWithPrivileges = group.getGroupSettings().getUsersWithPrivileges();

        if (group.getAdmin().equals(whoChanged)) {
            if (usersWithPrivileges.contains(user)) {
                usersWithPrivileges.remove(user);
            } else {
                usersWithPrivileges.add(user);
            }
        }

        group.getGroupSettings().setUsersWithPrivileges(usersWithPrivileges);
        groupSettingsRepository.save(group.getGroupSettings());

        return usersWithPrivileges;
    }
}
