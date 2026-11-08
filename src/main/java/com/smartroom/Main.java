package com.smartroom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartroom.controller.DeviceController;
import com.smartroom.persistence.JsonDB;
import com.smartroom.repository.JsonDeviceRepository;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;

public class Main {
    static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        JsonDB db = new JsonDB(Path.of("data", "db.json"), mapper);
        try {
            db.initialize();

            JsonDeviceRepository deviceRepository = new JsonDeviceRepository(db);
            deviceRepository.load();

            /*
            For this test, after intializing the database and loading into the repo,
            we start the server and register the controller.
             */
            HttpServer server = HttpServer.create(
                    new InetSocketAddress(8080),
                    0
            );

            server.createContext(
                    "/api/devices",
                    new DeviceController(deviceRepository, mapper)
            );

            server.start();

            System.out.println("SmartRoom API running at http://localhost:8080");

        } catch (IOException ioe) {
            System.err.println("Couldn't read file: " + ioe.getMessage());
        }

    }
}
