package com.smartroom.dto;

public record CreateUserRequest(
        String username,
        String passwordHash
) {
}