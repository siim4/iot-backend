package com.smartroom.repository;

import com.smartroom.model.Device;
import com.smartroom.persistence.JsonDB;

import java.util.List;
import java.util.UUID;

public class JsonDeviceRepository
        extends JsonRepository<Device>
        implements DeviceRepository {

    public JsonDeviceRepository(JsonDB db) {
        super(db, "devices", Device.class);
    }

    public void showAllDevices() {
        for (var device : findAll()) { // Also replaced direct access to entities map here.
            System.out.println(device);
            System.out.println();
        }
    }

    @Override
    protected UUID getId(Device device) {
        return device.getId();
    }

    public List<Device> findByOwnerId(UUID ownerId) {
        return findAll().stream()  // Replaced direct access to super.entities for improved encapsulation.
                .filter(device -> device.getOwnerId().equals(ownerId))
                .toList();
    }
}
