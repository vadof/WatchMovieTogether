package com.server.backend.components;

import com.server.backend.entity.Resolution;
import com.server.backend.repository.ResolutionRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final ResolutionRepository resolutionRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.initializeAllResolutions();
    }

    private void initializeAllResolutions() {
        if (((Collection<Resolution>) resolutionRepository.findAll()).size() == 0) {
            List<Resolution> resolutions = List.of(new Resolution("360p"),
                    new Resolution("480p"), new Resolution("720p"),
                    new Resolution("1080p"), new Resolution("1080p Ultra"),
                    new Resolution("1440p"), new Resolution("2160p"));

            resolutionRepository.saveAll(resolutions);
        }
    }
}
