package com.smartroom.model;

import java.util.UUID;

/***
 * Represents a registered user of the smart room.
 *
 * <p>Users have a unique identifier, a username and a
 * securely generated hash.
 *
 * <p>Update requires creating a new user with the same id.
 *
 * @param id unique identifier
 * @param username name used to identify the account
 * @param passwordHash secure non plaintext password hash
 */

public record User(
        UUID id,
        String username,
        String passwordHash
) {
    // Added validation in compact canonical constructor.
    public User {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
    }

    /*
     Overwritten constructor calls canonical to create new user with new id and
     follows construction path.
     */
    public User(String username, String passwordHash) {
        this(UUID.randomUUID(), username, passwordHash);
    }

    // I'm overriding toString to exclude passwordHash, exposed in default.
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + id + "\n"
                + "-".repeat(15) + "\n"
                + "Username: " + username;
    }
}
