package com.server.backend.repository;

import com.server.backend.entity.Chat;
import org.springframework.data.repository.CrudRepository;

public interface ChatRepository extends CrudRepository<Chat, Long> {
}
