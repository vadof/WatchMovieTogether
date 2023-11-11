package com.server.backend.configs;

import com.server.backend.entity.Resolution;
import com.server.backend.repository.ResolutionRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;

@Configuration
@AllArgsConstructor
public class DatabaseInitializer {

    private final ResolutionRepository resolutionRepository;

    @Bean
    public void addResolutionsToDatabase() {
        System.out.println("ADD RESOLUTION");
        if (((Collection<Resolution>) resolutionRepository.findAll()).isEmpty()) {
            List<Resolution> resolutions = List.of(new Resolution("360p"),
                    new Resolution("480p"), new Resolution("720p"),
                    new Resolution("1080p"), new Resolution("1080p Ultra"),
                    new Resolution("1440p"), new Resolution("2160p"));
            System.out.println("RESOLUTION ADDED");
            resolutionRepository.saveAll(resolutions);
        }
    }

}
