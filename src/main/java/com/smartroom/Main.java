package com.smartroom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartroom.controller.DeviceController;
import com.smartroom.model.Device;
import com.smartroom.persistence.JsonDB;
import com.smartroom.persistence.RelationalDB;
import com.smartroom.repository.JdbcDeviceRepository;
import com.smartroom.repository.Repository;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        JsonDB db = new JsonDB(Path.of("data", "db.json"), mapper);
        RelationalDB rdb = new RelationalDB(
                "jdbc:postgresql://localhost:5432/smartroom",
                "smartroom",
                "devpassword"
        );

        try (Connection connection = rdb.getConnection()) {
            System.out.println("Connected to PostgreSQL");
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
        try {

            //db.initialize();

            //Repository<Device> deviceRepository = new JsonDeviceRepository(db);
            //deviceRepository.load();
            Repository<Device> deviceRepository = new JdbcDeviceRepository(rdb);

            /*
            For this test, after initializing the database and loading into the repo,
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
