package com.smartroom.dto;

import com.smartroom.model.Device.Type;

import java.util.UUID;

/***
 * Data Transfer Object representing the permitted JSON
 *                                 for device creation.
 * @param name Name of the device.
 * @param type Type of the device.
 * @param ownerId User that owns the device.
 */

public record CreateDeviceRequest(
        String name,
        Type type,
        UUID ownerId
) {
}
