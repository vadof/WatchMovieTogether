package com.server.backend.repository;

import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<com.server.backend.entity.Message, Long> {
}
