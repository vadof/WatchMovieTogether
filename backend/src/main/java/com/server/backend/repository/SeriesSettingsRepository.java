package com.server.backend.repository;

import com.server.backend.entity.Series;
import com.server.backend.entity.SeriesSettings;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SeriesSettingsRepository extends CrudRepository<SeriesSettings, Long> {

    List<SeriesSettings> findAllBySelectedSeries(Series series);

}
