package com.server.backend.websocket;

import com.server.backend.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class GroupSession {

    private final Long groupId;
    private final Set<User> users = new HashSet<>();

    public GroupSession(Long groupId) {
        this.groupId = groupId;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public void removeUser(User user) {
        this.users.remove(user);
    }
}
