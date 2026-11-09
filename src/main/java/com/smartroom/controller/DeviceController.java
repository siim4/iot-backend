package com.smartroom.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartroom.dto.CreateDeviceRequest;
import com.smartroom.dto.UpdateDeviceRequest;
import com.smartroom.model.Device;
import com.smartroom.repository.Repository;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.UUID;

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

    @Override
    protected void handlePut(HttpExchange exchange, UUID id)
            throws IOException {

        UpdateDeviceRequest request;

        try {
            request = mapper.readValue(
                    exchange.getRequestBody(),
                    UpdateDeviceRequest.class
            );
        } catch (JsonProcessingException e) {
            sendStatus(exchange, 400);
            return;
        }

        if (request == null
                || request.ownerId() == null
                || request.name() == null
                || request.name().isBlank()
                || request.type() == null
                || request.online() == null
                || request.poweredOn() == null) {
            sendStatus(exchange, 400);
            return;
        }

        boolean exists;

        try {
            exists = repository.existsById(id);
        } catch (Exception e) {
            System.err.println("Failed to check entity existence: " + e.getMessage());
            sendStatus(exchange, 500);
            return;
        }

        if (!exists) {
            sendStatus(exchange, 404);
            return;
        }

        Device updated = new Device(
                id,
                request.ownerId(),
                request.name(),
                request.type(),
                request.online(),
                request.poweredOn()
        );

        byte[] json;
        boolean saved;

        try {
            json = mapper.writeValueAsBytes(updated);
            saved = repository.save(updated);
        } catch (Exception e) {
            System.err.println("Failed to update entity: " + e.getMessage());
            sendStatus(exchange, 500);
            return;
        }

        if (!saved) {
            sendStatus(exchange, 500);
            return;
        }

        sendJson(exchange, 200, json);
    }
}
