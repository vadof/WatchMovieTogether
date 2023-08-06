package com.server.backend.services;

import com.server.backend.entity.*;
import com.server.backend.jwt.JwtService;
import com.server.backend.repository.*;
import com.server.backend.requests.MovieSelectionRequest;
import com.server.backend.requests.SeriesSelectionRequest;
import com.server.backend.websocket.WebSocketService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupSettingsRepository groupSettingsRepository;
    private final ChatRepository chatRepository;

    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;

    private final UserRepository userRepository;

    private final MovieSettingsRepository movieSettingsRepository;
    private final SeriesSettingsRepository seriesSettingsRepository;

    private final JwtService jwtService;
    private final ChatService chatService;
    private final WebSocketService webSocketService;

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

        this.chatService.sendGroupCreateMessage(group.getId(), user.getUsername());

        return group;
    }

    @Transactional
    public void setUpMovieForGroup(MovieSelectionRequest msr) {
        try {
            Movie movie = movieRepository.findByLink(msr.getMovie().getLink()).orElseThrow();
            GroupSettings groupSettings = groupRepository.findById(msr.getGroupId()).orElseThrow()
                    .getGroupSettings();

            MovieSettings movieSettings = groupSettings.getMovieSettings();

           this.removeSeriesSettings(groupSettings);

            if (movieSettings != null && movie.getId().equals(movieSettings.getSelectedMovie().getId())) {
                this.changeSelectedMovieTranslation(msr.getGroupId(), msr.getSelectedTranslation());
            } else {
                Translation translation = movie.getTranslations()
                        .stream()
                        .filter(t -> t.equals(msr.getSelectedTranslation()))
                        .findFirst().orElseThrow();

                MovieSettings newMovieSettings = MovieSettings.builder()
                        .selectedMovie(movie)
                        .selectedTranslation(translation)
                        .build();
                this.movieSettingsRepository.save(newMovieSettings);

                groupSettings.setMovieSettings(newMovieSettings);
                this.groupSettingsRepository.save(groupSettings);

                this.chatService.sendMovieChangeMessage(msr.getGroupId(), movie.getName(), translation.getName());
                this.webSocketService.sendObjectByWebsocket("/group/" + msr.getGroupId() + "/movie", msr);
            }
        } catch (Exception e) {
            LOG.error("Failed to set up movie for group {}" + e.getMessage(), e);
        }
    }

    @Transactional
    public void setUpSeriesForGroup(SeriesSelectionRequest ssr) {
        try {
            GroupSettings groupSettings = groupRepository.findById(ssr.getGroupId()).orElseThrow().getGroupSettings();
            Series series = seriesRepository.findByLink(ssr.getSeries().getLink()).orElseThrow();

            SeriesSettings seriesSettings = groupSettings.getSeriesSettings();

            this.removeMovieSettings(groupSettings);

            SeriesTranslation seriesTranslation = series.getSeriesTranslations().stream()
                    .filter(t -> t.equals(ssr.getSelectedSeriesTranslation()))
                    .findFirst().orElseThrow();

            Season season = seriesTranslation.getSeasons().stream()
                    .filter(s -> s.getNumber().equals(ssr.getSeason().getNumber()))
                    .findFirst().orElseThrow();

            if (seriesSettings != null
                    && series.getId().equals(seriesSettings.getSelectedSeries().getId())) {

                if (!seriesSettings.getSelectedTranslation().equals(seriesTranslation)) {
                    seriesSettings.setSelectedTranslation(seriesTranslation);

                    this.chatService.sendSeriesTranslationChangeMessage(ssr.getGroupId(),
                            ssr.getSelectedSeriesTranslation().getName());
                }

                seriesSettings.setSelectedSeason(season);
                seriesSettings.setSelectedEpisode(ssr.getEpisode());

                this.chatService.sendSeriesEpisodeChange(ssr.getGroupId(),
                        String.valueOf(ssr.getSeason()), String.valueOf(ssr.getEpisode()));

                this.seriesSettingsRepository.save(seriesSettings);
            } else {
                seriesSettings = SeriesSettings.builder()
                        .selectedSeries(series)
                        .selectedTranslation(seriesTranslation)
                        .selectedSeason(season)
                        .selectedEpisode(ssr.getEpisode())
                        .build();
                this.seriesSettingsRepository.save(seriesSettings);

                groupSettings.setSeriesSettings(seriesSettings);
                this.groupSettingsRepository.save(groupSettings);

                this.chatService.sendSeriesChangeMesasge(ssr.getGroupId(), series.getName(),
                        seriesTranslation.getName());
            }
        } catch (Exception e) {
            LOG.error("Failed to set up series for group {}" + e.getMessage(), e);
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

            this.webSocketService.sendObjectByWebsocket("/group/" + groupId + "/user/add", user);

            this.chatService.sendUserJoinMessage(groupId, user.getUsername());
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
                this.chatService.sendUserLeaveMessage(groupId, removedUser.getUsername());
            } else {
                this.chatService.sendUserKickedMessage(groupId, removedUser.getUsername(),
                        whoRemovedUsername);
            }
            groupRepository.save(group);
        }

        return removedUser;
    }

    public void changeSelectedMovieTranslation(Long groupId, Translation translation) {
        Group group = groupRepository.findById(groupId).orElseThrow();
        MovieSettings movieSettings = group.getGroupSettings().getMovieSettings();
        if (movieSettings != null) {
            Movie movie = movieSettings.getSelectedMovie();

            if (!translation.equals(movieSettings.getSelectedTranslation())) {
                Translation translation1 = movie.getTranslations()
                        .stream()
                        .filter(t -> t.getName().equals(translation.getName()))
                        .findFirst().orElseThrow();

                movieSettings.setSelectedTranslation(translation1);
                movieSettingsRepository.save(movieSettings);

                this.webSocketService.sendObjectByWebsocket("/group/" + groupId + "/movie",
                        new MovieSelectionRequest(groupId, movie, translation1));

                this.chatService.sendTranslationChangeMessage(groupId, translation.getName());
            }
        }
    }

    private void removeMovieSettings(GroupSettings groupSettings) {
        MovieSettings movieSettings = groupSettings.getMovieSettings();
        if (movieSettings != null) {
            groupSettings.setMovieSettings(null);
            this.movieSettingsRepository.delete(movieSettings);
            this.groupSettingsRepository.save(groupSettings);
        }
    }

    private void removeSeriesSettings(GroupSettings groupSettings) {
        SeriesSettings seriesSettings = groupSettings.getSeriesSettings();
        if (seriesSettings != null) {
            groupSettings.setSeriesSettings(null);
            this.seriesSettingsRepository.delete(seriesSettings);
            this.groupSettingsRepository.save(groupSettings);
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
