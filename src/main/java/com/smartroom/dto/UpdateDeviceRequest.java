package com.smartroom.dto;

import com.smartroom.model.Device.Type;

import java.util.UUID;

/*
We use Boolean wrapper class so Jackson doesn't map a missing value to false.
It'd become null, which we can reject with 404 Bad Request.
 */

public record UpdateDeviceRequest(
        UUID ownerId,
        String name,
        Type type,
        Boolean online,
        Boolean poweredOn
) {
}