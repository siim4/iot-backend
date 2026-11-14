package com.smartroom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartroom.controller.DeviceController;
import com.smartroom.controller.ModeController;
import com.smartroom.controller.UserController;
import com.smartroom.persistence.RelationalDB;
import com.smartroom.model.Device;
import com.smartroom.model.Mode;
import com.smartroom.model.User;
import com.smartroom.repository.Repository;
import com.smartroom.repository.JdbcDeviceRepository;
import com.smartroom.repository.JdbcModeRepository;
import com.smartroom.repository.JdbcUserRepository;
import com.sun.net.httpserver.HttpServer;

import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.InetSocketAddress;
import java.io.IOException;

public class Main {

    // At least for now, we keep the exception reaching JVM in case of missing config.
    public static void main(String[] args) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        Properties properties = new Properties();

        //TODO: before eventual deploy, make path selectable.
        try (var input = Files.newInputStream(
                Path.of("config.properties")
        )) {

            properties.load(input);
        }

        String url = properties.getProperty("db.url");

        if (url == null || url.isBlank()) {

            throw new IllegalStateException("URL hasn't been configured for this environment.");
        }

        String username = properties.getProperty("db.username");

        if (username == null || username.isBlank()) {

            throw new IllegalStateException("No database user configured for this environment.");
        }

        String password = properties.getProperty("db.password");

        if (password == null || password.isBlank()) {

            throw new IllegalStateException("Database password missing.");
        }

        RelationalDB db = new RelationalDB(
                url,
                username,
                password
        );

        Repository<User> userRepository =
                new JdbcUserRepository(db);

        Repository<Device> deviceRepository =
                new JdbcDeviceRepository(db);

        Repository<Mode> modeRepository =
                new JdbcModeRepository(db);

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080),
                0
        );

        server.createContext(
                "/api/users",
                new UserController(userRepository, mapper)
        );

        server.createContext(
                "/api/devices",
                new DeviceController(deviceRepository, mapper)
        );

        server.createContext(
                "/api/modes",
                new ModeController(modeRepository, mapper)
        );

        server.start();

        System.out.println(
                "SmartRoom API running at http://localhost:8080"
        );
    }
}
