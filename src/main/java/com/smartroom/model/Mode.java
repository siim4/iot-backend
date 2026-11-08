package com.smartroom.model;

import java.util.UUID;

public record Mode(
        UUID id,
        UUID ownerId,
        String name
) {
    public Mode {
        if (id == null) {
            throw new IllegalArgumentException("Mode ID cannot be null.");
        }

        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID cannot be null.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Mode name cannot be null or blank.");
        }
        name = name.trim();
    }

    public Mode(UUID ownerId, String name) {
        this(UUID.randomUUID(), ownerId, name);
    }
}
