package com.smartroom.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartroom.dto.CreateModeRequest;
import com.smartroom.model.Mode;
import com.smartroom.repository.Repository;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.UUID;

public class ModeController extends Controller<Mode> {

    public ModeController(Repository<Mode> repository,
                          ObjectMapper mapper) {
        super(repository, mapper, Mode.class, "/api/modes");
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        CreateModeRequest request;
        Mode mode;
        byte[] json;

        try {
            request = mapper.readValue(
                    exchange.getRequestBody(),
                    CreateModeRequest.class
            );
        } catch (JsonProcessingException e) {
            sendStatus(exchange, 400);
            return;
        }
        if (request == null
                || request.ownerId() == null
                || request.name() == null
                || request.name().isBlank()
        ) {
            sendStatus(exchange, 400);
            return;
        }

        mode = new Mode(
                request.ownerId(),
                request.name()
        );

        try {
            json = mapper.writeValueAsBytes(mode);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize mode: " + e.getMessage());
            sendStatus(exchange, 500);
            return;
        }

        try {
            if (!repository.save(mode)) {
                sendStatus(exchange, 500);
                return;
            }
        } catch (Exception e) {
            System.err.println("Failed to create mode: " + e.getMessage());
            sendStatus(exchange, 500);
            return;
        }

        exchange.getResponseHeaders().set(
                "Location",
                basePath + "/" + mode.id()
        );

        sendJson(exchange, 201, json);

    }

    @Override
    protected void handlePut(HttpExchange exchange, UUID id) throws IOException {

        CreateModeRequest request;

        try {
            request = mapper.readValue(
                    exchange.getRequestBody(),
                    CreateModeRequest.class
            );
        } catch (JsonProcessingException e) {
            sendStatus(exchange, 400);
            return;
        }

        if (request == null
                || request.ownerId() == null
                || request.name() == null
                || request.name().isBlank()
        ) {
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
        Mode updated = new Mode(
                id,
                request.ownerId(),
                request.name()
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
