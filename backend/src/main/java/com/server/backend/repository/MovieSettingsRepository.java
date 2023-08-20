package com.server.backend.repository;

import com.server.backend.entity.Movie;
import com.server.backend.entity.MovieSettings;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MovieSettingsRepository extends CrudRepository<MovieSettings, Long> {

    List<MovieSettings> findAllBySelectedMovie(Movie movie);

}
