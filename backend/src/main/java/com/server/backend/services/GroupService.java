package com.server.backend.services;

import com.server.backend.entity.*;
import com.server.backend.jwt.JwtService;
import com.server.backend.repository.*;
import com.server.backend.requests.MovieSelectionRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    private static final Logger LOG = LoggerFactory.getLogger(GroupService.class);

    public Group createGroup(String name, String token) {
        User user = jwtService.getUserFromBearerToken(token).orElseThrow();

        Group group = new Group();
        group.setName(name);
        group.getUsers().add(user);
        group.setAdmin(user.getUsername());
        group.getUsers().add(user);

        Chat chat = new Chat();
        chatRepository.save(chat);
        group.setChat(chat);

        groupRepository.save(group);

        user.getGroups().add(group);
        userRepository.save(user);

        chatService.addSystemMessageToGroupChat(chat.getId(),
                chatService.generateGroupCreateMessage(user.getUsername()));

        return group;
    }

    public boolean setUpMovieForGroup(MovieSelectionRequest msr) {
        try {
            Movie movie = movieRepository.findByLink(msr.getMovie().getLink()).get();
            Group group = groupRepository.findById(msr.getGroupId()).get();
            Translation translation = movie.getTranslations()
                    .stream()
                    .filter(t -> t.equals(msr.getSelectedTranslation()))
                    .findFirst().get();

            GroupSettings groupSettings = new GroupSettings(movie, "0",
                    translation);

            groupSettingsRepository.save(groupSettings);

            GroupSettings oldGroupSettings = group.getGroupSettings();

            group.setGroupSettings(groupSettings);
            groupRepository.save(group);

            String movieChangeMessage = chatService.generateMovieChangeMessage(
                    movie.getName(), translation.getName());
            chatService.addSystemMessageToGroupChat(group.getChat().getId(), movieChangeMessage);

            if (oldGroupSettings != null) {
                groupSettingsRepository.delete(oldGroupSettings);
            }
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

            chatService.addSystemMessageToGroupChat(group.getChat().getId(),
                    chatService.generateUserJoinMessage(user.getUsername()));
        }
    }

    public void removeUserFromGroup(Long groupId, String username) {
        Group group = groupRepository.findById(groupId).orElseThrow();
        User user = userRepository.findByUsername(username).orElseThrow();

        group.getUsers().remove(user);
        user.getGroups().remove(group);
        userRepository.save(user);

        if (group.getUsers().isEmpty()) {
            groupRepository.delete(group);
        } else {
            if (group.getAdmin().equals(user.getUsername())) {
                group.setAdmin(group.getUsers().stream().findFirst().get().getUsername());
            }

            chatService.addSystemMessageToGroupChat(
                    group.getChat().getId(), chatService.generateUserLeaveMessage(username));

            groupRepository.save(group);
        }
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

            chatService.addSystemMessageToGroupChat(group.getChat().getId(),
                    chatService.generateTranslationChangeMessage(translation.getName()));
        }
    }
}
