package com.smartroom.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartroom.dto.CreateDeviceRequest;
import com.smartroom.model.Device;
import com.smartroom.repository.Repository;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class DeviceController extends Controller<Device> {
    public DeviceController(Repository<Device> repository,
                            ObjectMapper mapper) {
        super(repository, mapper, Device.class, "/api/devices");
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        CreateDeviceRequest request;
        Device device;
        byte[] json;
        try {
            request = mapper.readValue(
                    exchange.getRequestBody(),
                    CreateDeviceRequest.class
            );

        } catch (JsonProcessingException e) {
            sendStatus(exchange, 400);
            return;
        }
        if (request == null
                || request.name() == null
                || request.name().isBlank()
                || request.type() == null
                || request.ownerId() == null) {
            sendStatus(exchange, 400);
            return;
        }

        device = new Device(
                request.name(),
                request.type(),
                request.ownerId()
        );

        try {
            json = mapper.writeValueAsBytes(device);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize device: " + e.getMessage());
            sendStatus(exchange, 500);
            return;
        }


        try {
            if (!repository.save(device)) {
                sendStatus(exchange, 500);
                return;
            }
        } catch (Exception e) {
            System.err.println("Failed to create device: " + e.getMessage());
            sendStatus(exchange, 500);
            return;
        }

        exchange.getResponseHeaders().set(
                "Location",
                basePath + "/" + device.getId()
        );

        sendJson(exchange, 201, json);
    }
}
