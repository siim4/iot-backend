package com.smartroom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeviceTest {

    private Device light;

    @BeforeEach
    void setUp() {
        UUID ownerId = UUID.randomUUID();
        light = new Device("Light", Device.Type.LIGHT, ownerId);
    }

    @Test
    void newDeviceShouldBePoweredOff() {

        assertFalse(light.getPoweredOn());
    }

    @Test
    void powerOnShouldTurnDeviceOn() {
        light.powerOn();
        assertTrue(light.getPoweredOn());
    }

    @Test
    void togglePoweredOnShouldChangeState() {
        assertFalse(light.getPoweredOn());
        light.togglePoweredOn();
        assertTrue(light.getPoweredOn());
        light.togglePoweredOn();
        assertFalse(light.getPoweredOn());
    }
}
