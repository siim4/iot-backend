package com.smartroom.repository;

import com.smartroom.model.User;
import com.smartroom.persistence.JsonDB;

import java.util.UUID;

public class JsonUserRepository extends JsonRepository<User> {

    public JsonUserRepository(JsonDB db) {
        super(db, "users", User.class);
    }

    @Override
    protected UUID getId(User user) {
        return user.id();
    }
}
