package com.server.backend.services;

import com.server.backend.objects.LatestMovieRelease;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    List<LatestMovieRelease> latestMovieReleases = new ArrayList<>();

    @Scheduled(fixedDelay = 86400000L)
    private void updateLastReleases() {
        try {
            latestMovieReleases.clear();
            Document document = Jsoup.connect("https://rezka.ag/").get();
            List<Element> elements = document.select("#newest-slider-content").select(".b-content__inline_item");

            String REZKA_URL = "https://rezka.ag";
            for (int i = 0; i < 6; i++) {
                Element element = elements.get(i);
                String imgUrl = element.select("img").attr("src");
                String filmUrl = element.selectFirst(".b-content__inline_item-link")
                        .select("a").attr("href");
                String name = element.selectFirst(".b-content__inline_item-link")
                        .select("a").text();
                String additionalInfo = element.select(".misc").text();

                LatestMovieRelease latestMovieRelease =
                        new LatestMovieRelease(REZKA_URL + filmUrl, imgUrl, name, additionalInfo);

                latestMovieReleases.add(latestMovieRelease);
            }
            log.info("Updated last movie releases");
        } catch (Exception e) {
            log.error("Error getting last releases {}", e.getMessage());
        }
    }

    public List<LatestMovieRelease> getLatestMovieReleases() {
        return latestMovieReleases;
    }
}
