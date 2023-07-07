package com.server.backend.repository;

import com.server.backend.entity.Translation;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TranslationRepository extends CrudRepository<Translation, Long> {

    Optional<Translation> findByName(String name);

}
