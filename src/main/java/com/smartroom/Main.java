package com.smartroom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartroom.persistence.JsonDB;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        JsonDB db = new JsonDB(Path.of("data", "db.json"), mapper);
        try {
            db.initialize();
            JsonNode root = db.readJson();
            System.out.println(root);
            JsonNode devices = root.get("devices");
            System.out.println(devices);
        } catch (IOException ioe) {
            System.err.println("Couldn't read file: " + ioe.getMessage());
        }

    }
}
