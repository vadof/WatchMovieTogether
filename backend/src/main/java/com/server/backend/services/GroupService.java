package com.server.backend.services;

import com.server.backend.entity.*;
import com.server.backend.jwt.JwtService;
import com.server.backend.repository.GroupRepository;
import com.server.backend.repository.GroupSettingsRepository;
import com.server.backend.repository.MovieRepository;
import com.server.backend.repository.UserRepository;
import com.server.backend.requests.MovieSelectionRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupSettingsRepository groupSettingsRepository;
    private final MovieRepository movieRepository;
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

            if (oldGroupSettings != null) {
                groupSettingsRepository.delete(oldGroupSettings);
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
