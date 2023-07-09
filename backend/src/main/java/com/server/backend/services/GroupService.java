package com.server.backend.services;

import com.server.backend.entity.*;
import com.server.backend.jwt.JwtService;
import com.server.backend.repository.GroupRepository;
import com.server.backend.repository.GroupSettingsRepository;
import com.server.backend.repository.MovieRepository;
import com.server.backend.repository.UserRepository;
import com.server.backend.requests.MovieSelectionRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupSettingsRepository groupSettingsRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private static final Logger LOG = LoggerFactory.getLogger(GroupService.class);

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

    public boolean setUpMovieForGroup(MovieSelectionRequest msr) {
        try {
            Movie movie = movieRepository.findByLink(msr.getMovie().getLink()).get();
            System.out.println(movie.getName());
            Group group = groupRepository.findById(msr.getGroupId()).get();
            System.out.println(group.getName());
            Translation translation = movie.getTranslations()
                    .stream()
                    .filter(t -> t.equals(msr.getSelectedTranslation()))
                    .findFirst().get();
            System.out.println(translation.getName());

            GroupSettings groupSettings = new GroupSettings(movie, "0",
                    translation);

            groupSettingsRepository.save(groupSettings);

            GroupSettings oldGroupSettings = group.getGroupSettings();

            group.setGroupSettings(groupSettings);
            groupRepository.save(group);

            if (oldGroupSettings != null) {
                groupSettingsRepository.delete(oldGroupSettings);
            }
            return true;
        } catch (Exception e) {
            LOG.error("Failed to set up movie for group " + e.getMessage());
            return false;
        }
    }
}
