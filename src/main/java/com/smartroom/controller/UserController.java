package com.smartroom.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartroom.dto.CreateUserRequest;
import com.smartroom.dto.UserResponse;
import com.smartroom.model.User;
import com.smartroom.repository.Repository;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserController extends Controller<User> {

    public UserController(
            Repository<User> repository,
            ObjectMapper mapper
    ) {
        super(repository, mapper, User.class, "/api/users");
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.id(),
                user.username()
        );
    }

    @Override
    protected void handleGetAll(HttpExchange exchange)
            throws IOException {

        List<UserResponse> responses;

        try {
            responses = repository.findAll()
                    .stream()
                    .map(this::toResponse)
                    .toList();
        } catch (Exception e) {
            System.err.println("Failed to list users: " + e.getMessage());
            sendStatus(exchange, 500);
            return;
        }

        byte[] json;

        try {
            json = mapper.writeValueAsBytes(responses);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize users: " + e.getMessage());
            sendStatus(exchange, 500);
            return;
        }

        sendJson(exchange, 200, json);
    }

    @Override
    protected void handleGetById(
            HttpExchange exchange,
            UUID id
    ) throws IOException {

        Optional<User> result;

        try {
            result = repository.findById(id);
        } catch (Exception e) {
            System.err.println("Failed to find user: " + e.getMessage());
            sendStatus(exchange, 500);
            return;
        }

        if (result.isEmpty()) {
            sendStatus(exchange, 404);
            return;
        }

        byte[] json;

        try {
            json = mapper.writeValueAsBytes(toResponse(result.get()));
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize user: " + e.getMessage());
            sendStatus(exchange, 500);
            return;
        }

        sendJson(exchange, 200, json);
    }

    @Override
    protected void handlePost(HttpExchange exchange)
            throws IOException {

        CreateUserRequest request;

        try {
            request = mapper.readValue(
                    exchange.getRequestBody(),
                    CreateUserRequest.class
            );
        } catch (JsonProcessingException e) {
            sendStatus(exchange, 400);
            return;
        }

        if (request == null
                || request.username() == null
                || request.username().isBlank()
                || request.passwordHash() == null
                || request.passwordHash().isBlank()) {
            sendStatus(exchange, 400);
            return;
        }

        User user = new User(
                request.username(),
                request.passwordHash()
        );

        byte[] json;

        try {
            json = mapper.writeValueAsBytes(toResponse(user));
        } catch (JsonProcessingException e) {
            sendStatus(exchange, 500);
            return;
        }

        boolean saved;

        try {
            saved = repository.save(user);
        } catch (Exception e) {
            System.err.println("Failed to create user: " + e.getMessage());
            sendStatus(exchange, 500);
            return;
        }

        if (!saved) {
            sendStatus(exchange, 500);
            return;
        }

        exchange.getResponseHeaders().set(
                "Location",
                basePath + "/" + user.id()
        );

        sendJson(exchange, 201, json);
    }

    @Override
    protected void handlePut(
            HttpExchange exchange,
            UUID id
    ) throws IOException {

        CreateUserRequest request;

        try {
            request = mapper.readValue(
                    exchange.getRequestBody(),
                    CreateUserRequest.class
            );
        } catch (JsonProcessingException e) {
            sendStatus(exchange, 400);
            return;
        }

        if (request == null
                || request.username() == null
                || request.username().isBlank()
                || request.passwordHash() == null
                || request.passwordHash().isBlank()) {
            sendStatus(exchange, 400);
            return;
        }

        boolean exists;

        try {
            exists = repository.existsById(id);
        } catch (Exception e) {
            System.err.println("Failed to check user: " + e.getMessage());
            sendStatus(exchange, 500);
            return;
        }

        if (!exists) {
            sendStatus(exchange, 404);
            return;
        }

        User updated = new User(
                id,
                request.username(),
                request.passwordHash()
        );

        byte[] json;

        try {
            json = mapper.writeValueAsBytes(toResponse(updated));
        } catch (JsonProcessingException e) {
            sendStatus(exchange, 500);
            return;
        }

        boolean saved;

        try {
            saved = repository.save(updated);
        } catch (Exception e) {
            System.err.println("Failed to update user: " + e.getMessage());
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