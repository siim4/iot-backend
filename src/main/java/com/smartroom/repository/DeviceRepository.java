package com.smartroom.repository;

import com.smartroom.model.Device;

import java.util.*;

// This would become the device repo.
public class DeviceRepository implements Repository<Device> {

    private final Map<UUID, Device> devices;

    public DeviceRepository() {
        this.devices = new HashMap<>();
    }

    public boolean addDevice(Device d) {
        boolean isAdded = false;
        if (existsById(d.getID())) {
            System.out.println("Device already exists.");
        } else {
            isAdded = true;
            devices.put(d.getID(), d);
        }
        return isAdded;
    }

    public Optional<Device> findById(UUID id) {
        return Optional.ofNullable(devices.get(id));
    }

    @Override
    public long count() {
        return devices.size();
    }
    
    @Override
    public boolean save(Device device) {
        devices.put(device.getID(), device);
        return true;
    }

    @Override
    public List<Device> findAll() {
        return new ArrayList<>(devices.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return devices.containsKey(id);
    }

    @Override
    public boolean deleteById(UUID id) {
        boolean isDeleted = false;
        if (existsById(id)) {
            devices.remove(id);
            isDeleted = true;
        }
        return isDeleted;
    }

    @Override
    public boolean delete(Device device) {
        Optional<Device> target = Optional.of(device);

        return false;
    }

    public void showAllDevices() {
        for (var device : devices.entrySet()) {
            System.out.println(device.getValue());
            System.out.println();
        }
    }
}
