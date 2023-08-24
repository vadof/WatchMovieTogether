package com.server.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.backend.entity.*;
import com.server.backend.repository.*;
import com.server.backend.websocket.WebSocketService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class SeriesService {

    private final GroupRepository groupRepository;
    private final SeriesRepository seriesRepository;
    private final SeriesTranslationRepository seriesTranslationRepository;
    private final SeriesSettingsRepository seriesSettingsRepository;
    private final SeasonRepository seasonRepository;
    private final ResolutionRepository resolutionRepository;

    private final HTTPService httpService;
    private final ChatService chatService;
    private final WebSocketService webSocketService;

    private final String REZKA_API_URL = "http://localhost:5000/api";

    public Optional<Series> getSeries(String link) {
        if (link.contains("#")) {
            link = link.substring(0, link.indexOf("#"));
        }
        Optional<Series> optionalSeries = seriesRepository.findByLink(link);
        if (optionalSeries.isEmpty()) {
            optionalSeries = this.sendSeriesRequest(link);
            optionalSeries.ifPresent(this::saveSeries);
        } else {
            Series series = optionalSeries.get();
            series.addSearch();
            seriesRepository.save(series);
        }
        return optionalSeries;
    }

    private Optional<Series> sendSeriesRequest(String link) {
        String requestBody = String.format("{\"url\":\"%s\"}", link);
        String seriesJsonString = httpService.sendPostRequest(requestBody, REZKA_API_URL + "/series");
        return parseSeriesFromString(seriesJsonString);
    }

    private Optional<Series> parseSeriesFromString(String s) {
        try {
            String elementToCut = "{\"series\":";
            s = s.substring(elementToCut.length());

            ObjectMapper objectMapper = new ObjectMapper();
            return Optional.of(objectMapper.readValue(s, Series.class));
        } catch (Exception e) {
            log.error("Error while parsing a series object " + e.getMessage());
        }
        return Optional.empty();
    }

    @Transactional
    private void saveSeries(Series series) {
        try {
            for (SeriesTranslation seriesTranslation : series.getSeriesTranslations()) {
                List<Resolution> resolutions = new ArrayList<>();
                for (Resolution r : seriesTranslation.getResolutions()) {
                    resolutions.add(resolutionRepository.findByValue(r.getValue()));
                }
                seriesTranslation.setResolutions(resolutions);
                seasonRepository.saveAll(seriesTranslation.getSeasons());

                seriesTranslationRepository.save(seriesTranslation);
            }

            seriesRepository.save(series);
            log.info("Series saved to database: {}", series.getLink());
        } catch (Exception e) {
            log.error("Error occurred while saving the series: {}", series.getLink(), e);
        }
    }

    public Optional<String> getSeriesStreamLink(Long groupId, String resolution,
                                                Integer season, Integer episode) {
        try {
            Optional<String> savedLink = this.webSocketService.getStreamLink(groupId, resolution);
            if (savedLink.isPresent()) {
                return savedLink;
            }

            Group group = this.groupRepository.findById(groupId).orElseThrow();
            SeriesSettings seriesSettings = group.getGroupSettings().getSeriesSettings();

            String seriesUrl = seriesSettings.getSelectedSeries().getLink();
            SeriesTranslation seriesTranslation = seriesSettings.getSelectedTranslation();

            String requestBody = String.format("{\"url\":\"%s\",\"translation\":\"%s\"," +
                            "\"resolution\":\"%s\",\"season\":\"%s\",\"episode\":\"%s\"}",
                    seriesUrl, seriesTranslation.getName(), resolution, season, episode);

            String streamLink = this.httpService.sendPostRequest(requestBody, REZKA_API_URL + "/series/link");
            streamLink = streamLink.substring(streamLink.indexOf("http"), streamLink.lastIndexOf(".mp4") + 4);

            return Optional.of(streamLink);
        } catch (Exception e) {
            log.error("Error getting series stream link {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Series> updateSeriesInfo(Series series) {
        try {
            series = this.seriesRepository.findByLink(series.getLink()).orElseThrow();
            Series updatedSeries = this.sendSeriesRequest(series.getLink()).orElseThrow();

            if (!series.equals(updatedSeries)) {
                series.setName(updatedSeries.getName());

                for (SeriesTranslation translation : updatedSeries.getSeriesTranslations()) {
                    List<Resolution> resolutions = new ArrayList<>();
                    for (Resolution resolution : translation.getResolutions()) {
                        resolutions.add(resolutionRepository.findByValue(resolution.getValue()));
                    }
                    translation.setResolutions(resolutions);
                    seasonRepository.saveAll(translation.getSeasons());
                }

                this.seriesTranslationRepository.saveAll(updatedSeries.getSeriesTranslations());
                this.changeSelectedTranslationForAllGroups(series, updatedSeries.getSeriesTranslations());

                this.seriesTranslationRepository.deleteAll(series.getSeriesTranslations());
                series.setSeriesTranslations(updatedSeries.getSeriesTranslations());

                this.seriesRepository.save(series);

                log.info("Updated series {}", series.getLink());
            }

            return Optional.of(updatedSeries);
        } catch (Exception e) {
            log.error("Error updating series with link {}, Error: {}", series.getLink(), e.getMessage());
            return Optional.empty();
        }
    }

    private void changeSelectedTranslationForAllGroups(Series series, List<SeriesTranslation> seriesTranslations) {
        List<SeriesSettings> seriesSettingsList = this.seriesSettingsRepository.findAllBySelectedSeries(series);

        for (SeriesSettings ss : seriesSettingsList) {
            boolean wasChanged = false;

            Optional<SeriesTranslation> sameTranslation = seriesTranslations.stream()
                    .filter(st -> st.getName().equals(ss.getSelectedTranslation().getName()))
                    .findFirst();

            if (sameTranslation.isPresent()) {
                Optional<Season> sameSeason = sameTranslation.get().getSeasons().stream()
                        .filter(s -> s.getNumber().equals(ss.getSelectedSeason().getNumber())
                                && s.getEpisodes() >= ss.getSelectedEpisode())
                        .findFirst();

                if (sameSeason.isPresent()) {
                    ss.setSelectedSeason(sameSeason.get());
                } else {
                    wasChanged = true;
                    Season season = sameTranslation.get().getSeasons().get(0);
                    ss.setSelectedSeason(season);
                    ss.setSelectedEpisode(1);
                }

                ss.setSelectedTranslation(sameTranslation.get());
            } else {
                wasChanged = true;
                SeriesTranslation seriesTranslation = seriesTranslations.get(0);
                ss.setSelectedTranslation(seriesTranslation);
                ss.setSelectedSeason(seriesTranslation.getSeasons().get(0));
                ss.setSelectedEpisode(1);
            }

            if (wasChanged) {
                Long groupId = this.groupRepository.findBySeriesSettings(ss).getId();
                this.chatService.sendSeriesUpdateMessage(groupId);
                this.webSocketService.sendObjectByWebsocket("/group/" + groupId + "/series", ss);
            }
        }

        this.seriesSettingsRepository.saveAll(seriesSettingsList);
    }
}
