package com.smartroom.dto;

import java.util.UUID;

public record CreateModeRequest(UUID ownerId, String name) {
}
