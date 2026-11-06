package com.smartroom.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Device {
    private final UUID ID;
    private String name;
    private Type type;
    private boolean online;
    private boolean poweredOn;


    public Device(String name, Type type) {
        this.ID = UUID.randomUUID();
        this.setName(name);
        this.type = type;
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
            @JsonProperty("name") String name,
            @JsonProperty("type") Type type,
            @JsonProperty("online") boolean online,
            @JsonProperty("poweredOn") boolean poweredOn
    ) {
        this.ID = id;
        this.name = name;
        this.type = type;
        this.online = online;
        this.poweredOn = poweredOn;
    }

    public UUID getID() throws NullPointerException {
        if (ID == null) {
            throw new NullPointerException("ID was not assigned.");
        }
        return ID;
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
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("The name can't be null or empty");
        } else {
            this.name = name;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + ID + "\n"
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