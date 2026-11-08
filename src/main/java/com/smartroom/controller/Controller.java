package com.smartroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartroom.repository.Repository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public abstract class Controller<T> implements HttpHandler {
    protected final Repository<T> repository;
    protected final ObjectMapper mapper;
    protected final Class<T> entityType;
    protected final String basePath;

    protected Controller(
            Repository<T> repository,
            ObjectMapper mapper,
            Class<T> entityType,
            String basePath
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.entityType = entityType;
        this.basePath = basePath;

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (path.equals(basePath)) {
            switch (method) {
                case "GET" -> handleGetAll(exchange);
                case "POST" -> handlePost(exchange);
                default -> methodNotAllowed(exchange);
            }
            return;
        } else if (path.startsWith(basePath + "/")) {
            String idString = path.substring(basePath.length() + 1);
            if (idString.isEmpty() || idString.contains("/")) {
                sendStatus(exchange, 404);
                return;
            }
            UUID id;
            try {
                id = UUID.fromString(idString);
            } catch (IllegalArgumentException e) {
                sendStatus(exchange, 400);
                return;
            }

            switch (method) {
                case "GET" -> handleGetById(exchange, id);
                default -> methodNotAllowed(exchange);
            }
            return;
        }
        sendStatus(exchange, 404);
    }

    protected void handleGetAll(HttpExchange exchange) throws IOException {
        byte[] json;
        try {
            json = mapper.writeValueAsBytes(repository.findAll());
        } catch (Exception e) {
            System.err.println("Failed to retrieve entities: " + e.getMessage());
            sendStatus(exchange, 500);
            return;
        }
        sendJson(exchange, 200, json);
    }

    protected void handleGetById(HttpExchange exchange,
                                 UUID id
    ) throws IOException {
        byte[] json;

        try {
            Optional<T> entity = repository.findById(id);

            if (entity.isEmpty()) {
                sendStatus(exchange, 404);
                return;
            }

            json = mapper.writeValueAsBytes(entity.get());

        } catch (Exception e) {
            System.err.println("Failed to retrieve entity: " + e.getMessage());
            sendStatus(exchange, 500);
            return;
        }
        sendJson(exchange, 200, json);
    }

    protected void sendJson(
            HttpExchange exchange,
            int status,
            byte[] json
    ) throws IOException {
        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, json.length);

        try (var output = exchange.getResponseBody()) {
            output.write(json);
        }
    }

    protected void sendStatus(
            HttpExchange exchange,
            int status
    ) throws IOException {
        exchange.sendResponseHeaders(status, -1);
        exchange.close();
    }

    protected void methodNotAllowed(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Allow", "GET");
        sendStatus(exchange, 405);
    }

    protected abstract void handlePost(HttpExchange exchange) throws IOException;
}
