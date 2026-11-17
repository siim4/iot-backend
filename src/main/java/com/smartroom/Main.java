package com.smartroom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartroom.controller.DeviceController;
import com.smartroom.controller.ModeController;
import com.smartroom.controller.UserController;
import com.smartroom.model.Device;
import com.smartroom.model.Mode;
import com.smartroom.model.User;
import com.smartroom.persistence.RelationalDB;
import com.smartroom.repository.JdbcDeviceRepository;
import com.smartroom.repository.JdbcModeRepository;
import com.smartroom.repository.JdbcUserRepository;
import com.smartroom.repository.Repository;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Main {

//    static void main(String[] args) throws IOException {
//        Path path = Path.of("data", "db.json");
//        Files.createDirectories(path.toAbsolutePath().getParent());
//        ObjectMapper mapper = new ObjectMapper();
//        JsonDB db = new JsonDB(path, mapper);
//        db.initialize();
//        JsonRepository<Device> deviceRepository = new JsonDeviceRepository(db);
//        JsonRepository<User> userJsonRepository = new JsonUserRepository(db);
//        deviceRepository.load();
//        HttpServer server = HttpServer.create(
//                new InetSocketAddress(8080),
//                0
//        );
//        server.createContext(
//                "/api/devices",
//                new DeviceController(deviceRepository, mapper)
//        );
//        server.createContext(
//                "/api/users",
//                new UserController(userJsonRepository, mapper)
//        );
//
//        server.start();
//        System.out.println("Listening on port 8080...");
//    }

    //     At least for now, we keep the exception reaching JVM in case of missing config.
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

        /*
        When termination signal is received we stop the server
        and shutdown the connection pool from the database.
         */
        //TODO: coordinate better shutdown/cleanup for prod.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(0);
            db.close();
        }));

        server.start();

        System.out.println(
                "SmartRoom API running at http://localhost:8080"
        );
    }
}
