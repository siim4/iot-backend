package com.smartroom.repository;

import com.smartroom.model.Mode;
import com.smartroom.persistence.JsonDB;

import java.util.List;
import java.util.UUID;

public class JsonModeRepository extends JsonRepository<Mode> {

    public JsonModeRepository(JsonDB db) {
        super(db, "modes", Mode.class);
    }

    @Override
    protected UUID getId(Mode mode) {
        return mode.id();
    }

    public List<Mode> findByOwnerId(UUID ownerId) {
        return entities.values().stream()
                .filter(mode -> mode.ownerId().equals(ownerId))
                .toList();
    }
}
