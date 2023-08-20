package com.server.backend.repository;

import com.server.backend.entity.Group;
import com.server.backend.entity.MovieSettings;
import com.server.backend.entity.SeriesSettings;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends CrudRepository<Group, Long> {

    @Query("SELECT g FROM Group g WHERE g.groupSettings.movieSettings = :movieSettings")
    Group findByMovieSettings(@Param("movieSettings") MovieSettings movieSettings);

    @Query("SELECT g FROM Group g WHERE g.groupSettings.seriesSettings = :seriesSettings")
    Group findBySeriesSettings(@Param("seriesSettings") SeriesSettings seriesSettings);
}
