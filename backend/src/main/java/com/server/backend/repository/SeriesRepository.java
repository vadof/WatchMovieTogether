package com.server.backend.repository;

import com.server.backend.entity.Series;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SeriesRepository extends CrudRepository<Series, Long> {

    Optional<Series> findByLink(String link);

}
