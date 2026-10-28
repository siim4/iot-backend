package com.smartroom.model;

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

    public UUID getID() throws NullPointerException {
        if (ID == null) {
            throw new NullPointerException("ID was not assigned.");
        }
        return ID;
    }

    public String getName() {
        return name;
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
