package com.smartroom.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

public class Device {
    private final UUID id;
    private String name;
    private Type type;
    private final UUID ownerId;
    private boolean online;
    private boolean poweredOn;


    public Device(String name, Type type, UUID ownerId) {
        this.id = UUID.randomUUID();
        this.setName(name);
        this.ownerId = Objects.requireNonNull(ownerId, "Owner ID cannot be null");
        this.type = Objects.requireNonNull(type, "Device type cannot be null");
        this.online = false;
        this.poweredOn = false;
    }

    /**
     * Restores device from stored data.
     *
     * <p>Gets values from keys in JSON object.</p>
     * @param id
     * @param name
     * @param type
     * @param online
     * @param poweredOn
     */
    @JsonCreator
    public Device(
            @JsonProperty("id") UUID id,
            @JsonProperty("ownerId") UUID ownerId,
            @JsonProperty("name") String name,
            @JsonProperty("type") Type type,
            @JsonProperty("online") boolean online,
            @JsonProperty("poweredOn") boolean poweredOn
    ) {
        this.id = Objects.requireNonNull(id, "Device ID cannot be null");
        this.ownerId = Objects.requireNonNull(ownerId, "Owner ID cannot be null");
        this.type = Objects.requireNonNull(type, "Device type cannot be null");

        this.setName(name);
        this.online = online;
        this.poweredOn = poweredOn;
    }

    public UUID getId() {
        if (id == null) {
            throw new NullPointerException("ID was not assigned.");
        }
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public boolean getPoweredOn() {
        return poweredOn;
    }

    public Type getType() {
        return type;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void powerOn() {
        this.poweredOn = true;
    }

    public void powerOff() {
        this.poweredOn = false;
    }

    public void togglePoweredOn() {
        this.poweredOn = !poweredOn;
    }

    public void setName(String name) throws IllegalArgumentException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The name can't be null or blank.");
        } else {
            this.name = name;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + id + "\n"
                + "-".repeat(15) + "\n"
                + "Device name: " + name + "\n"
                + "Device type: " + type + "\n"
                + "Status: " + (online ? "online" : "offline") + "\n"
                + "Power: " + (poweredOn ? "on" : "off");
    }

    public enum Type {
        LIGHT,
        AC,
        TV,
        VIDEO_GAME

    }
}