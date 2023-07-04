package com.server.backend.repository;

import com.server.backend.entity.Group;
import com.server.backend.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface GroupRepository extends CrudRepository<Group, Long> {

    @Query("SELECT g FROM Group g JOIN g.users u WHERE u = :user")
    List<Group> findAllByUserIn(User user);

}
