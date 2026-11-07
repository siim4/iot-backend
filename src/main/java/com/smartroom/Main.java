package com.smartroom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartroom.model.Device;
import com.smartroom.model.User;
import com.smartroom.persistence.JsonDB;
import com.smartroom.repository.JsonDeviceRepository;
import com.smartroom.repository.JsonUserRepository;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        JsonDB db = new JsonDB(Path.of("data", "db.json"), mapper);
        Device light = new Device("Bedroom light", Device.Type.LIGHT);
        User sima = new User("halal_detachment", "1234");
        JsonDeviceRepository deviceRepo = new JsonDeviceRepository(db);
        JsonUserRepository userRepo = new JsonUserRepository(db);
        try {
            db.initialize();
            JsonDeviceRepository freshDeviceRepo = new JsonDeviceRepository(db);
            JsonUserRepository freshUserRepo = new JsonUserRepository(db);
            deviceRepo.save(light);
            userRepo.save(sima);

            freshDeviceRepo.load();
            freshUserRepo.load();

            System.out.println(
                    "Device restored: " + freshDeviceRepo.existsById(light.getID())
            );

            System.out.println(
                    "User restored: " + freshUserRepo.existsById(sima.id())
            );

        } catch (IOException ioe) {
            System.err.println("Couldn't read file: " + ioe.getMessage());
        }

    }
}
