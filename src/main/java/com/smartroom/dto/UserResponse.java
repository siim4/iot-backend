package com.smartroom.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username
) {
}