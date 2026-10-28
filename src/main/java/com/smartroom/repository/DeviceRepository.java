package com.smartroom.repository;

import com.smartroom.model.Device;

import java.util.*;

// This would become the device repo.
public class DeviceRepository implements Repository<Device> {

    private final Map<UUID, Device> devices;


    public DeviceRepository() {
        this.devices = new HashMap<>();
    }

//    public Room() {
//        light = new Device("Bedroom light", Device.Type.LIGHT);
//        ac = new Device("LG Portable Air Conditioner", Device.Type.AC);
//        tv = new Device("Samsung S95H OLED", Device.Type.TV);
//        vgConsole = new Device("PS5 Slim (Digital)", Device.Type.VIDEO_GAME);
//        devices.add(light);
//        devices.add(ac);
//        devices.add(tv);
//        devices.add(vgConsole);
//    }

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

        Optional<Device> target = null;
        if (devices.isEmpty()) {
            System.out.println("No available devices.");
            target = Optional.empty();
        }

        for (var device : devices.entrySet()) {
            if (device.getKey().equals(id)) {
                target = Optional.ofNullable(device.getValue());
            } else {
                target = Optional.empty();
                System.out.println("Couldn't find a device with that ID.");
            }
        }
        return target;
    }

    @Override
    public long count() {
        return devices.size();
    }

    @Override
    public boolean save(Map<UUID, Device> devices) {
        return false;
    }

    @Override
    public boolean save(Device device) {
        return false;
    }

    @Override
    public List<Device> findAll() {
        return List.of();
    }

    @Override
    public boolean existsById(UUID id) {
        boolean isFound = false;
        for (Map.Entry<UUID, Device> entry : devices.entrySet()) {
            if (entry.getKey().equals(id)) {
                isFound = true;
            }
        }
        return isFound;
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
