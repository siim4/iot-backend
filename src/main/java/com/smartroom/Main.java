package com.smartroom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.smartroom.model.Device;
import com.smartroom.persistence.JsonDB;
import com.smartroom.repository.JsonDeviceRepository;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        JsonDB db = new JsonDB(Path.of("data", "db.json"), mapper);
        Device light = new Device("Bedroom light", Device.Type.LIGHT);
        JsonDeviceRepository deviceRepo = new JsonDeviceRepository(db);
        try {
            db.initialize();
            JsonNode root = db.readJson();
            System.out.println(root);
            JsonNode devices = root.get("devices");
            ArrayNode deviceArray = (ArrayNode) root.get("devices");
            deviceArray.forEach(System.out::println);
            System.out.println(devices);
            deviceRepo.save(light);

        } catch (IOException ioe) {
            System.err.println("Couldn't read file: " + ioe.getMessage());
        }

    }
}
