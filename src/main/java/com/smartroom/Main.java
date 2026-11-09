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

import java.net.InetSocketAddress;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        RelationalDB db = new RelationalDB(
                "jdbc:postgresql://localhost:5432/smartroom",
                "smartroom",
                "devpassword"
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