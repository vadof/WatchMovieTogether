package com.server.backend.services;

import com.server.backend.entity.*;
import com.server.backend.repository.GroupRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class SeriesService {

    private final GroupRepository groupRepository;
    private final HTTPSerivce httpSerivce;

    private final String REZKA_API_URL = "http://localhost:5000/api";

    public Optional<String> getSeriesStreamLink(Long groupId, String resolution,
                                                Integer season, Integer episode) {
        try {
            Group group = this.groupRepository.findById(groupId).orElseThrow();
            SeriesSettings seriesSettings = group.getGroupSettings().getSeriesSettings();

            String seriesUrl = seriesSettings.getSelectedSeries().getLink();
            SeriesTranslation seriesTranslation = seriesSettings.getSelectedTranslation();

            String requestBody = String.format("{\"url\":\"%s\",\"translation\":\"%s\"," +
                            "\"resolution\":\"%s\",\"season\":\"%s\",\"episode\":\"%s\"}",
                    seriesUrl, seriesTranslation.getName(), resolution, season, episode);

            String streamLink = this.httpSerivce.sendPostRequest(requestBody, REZKA_API_URL + "/series/link");
            streamLink = streamLink.substring(streamLink.indexOf("http"), streamLink.lastIndexOf(".mp4") + 4);

            return Optional.of(streamLink);
        } catch (Exception e) {
            log.error("Error getting series stream link {}", e.getMessage());
        }
        return Optional.empty();
    }

}
