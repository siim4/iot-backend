package com.smartroom;

import com.smartroom.model.Device;
import com.smartroom.repository.DeviceRepository;

import java.util.Optional;
import java.util.UUID;

public class Main {
    static void main(String[] args) {
        DeviceRepository repository = new DeviceRepository();
        try {
            Device light = new Device("Bedroom Light", Device.Type.LIGHT);
            repository.addDevice(light);
            //repository.showAllDevices();
            Optional<Device> other = repository.findById(light.getID());
            Device device = other.get();
            repository.addDevice(device);
            repository.showAllDevices();

            Device device2 = null;
            UUID id = null;
            Optional<Device> d3 = repository.findById(id);
            if (d3.isPresent()) {
                Device d4 = d3.get();
            } else {
                System.out.println("device couldn't be found by ID.");
            }


        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Enter a valid name.");
        }
    }
}
