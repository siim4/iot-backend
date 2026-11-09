package com.smartroom.repository;

import com.smartroom.model.Device;
import com.smartroom.persistence.JsonDB;

import java.util.List;
import java.util.UUID;

public class JsonDeviceRepository extends JsonRepository<Device> {

    public JsonDeviceRepository(JsonDB db) {
        super(db, "devices", Device.class);
    }

    public void showAllDevices() {
        for (var device : entities.entrySet()) {
            System.out.println(device.getValue());
            System.out.println();
        }
    }

    @Override
    protected UUID getId(Device device) {
        return device.getId();
    }

    public List<Device> findByOwnerId(UUID ownerId) {
        return entities.values().stream()
                .filter(device -> device.getOwnerId().equals(ownerId))
                .toList();
    }
}
