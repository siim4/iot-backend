package com.smartroom.repository;

import com.smartroom.model.User;
import com.smartroom.persistence.JsonDB;

import java.util.Optional;
import java.util.UUID;

public class JsonUserRepository extends JsonRepository<User> {

    public JsonUserRepository(JsonDB db) {
        super(db, "users", User.class);
    }

    @Override
    protected UUID getId(User user) {
        return user.id();
    }

    public Optional<User> findByUsername(String username) {
        return entities.values().stream()
                .filter(user -> user.username().equals(username))
                .findFirst();
    }
}
