package com.smartroom.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartroom.model.Device;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonDB {
    private final Path path;
    private final ObjectMapper mapper;

    public JsonDB(Path path, ObjectMapper mapper) {
        this.path = path;
        this.mapper = mapper;
    }

    public Path getPath() {
        return path;
    }

    public String readFile() throws IOException {

        return Files.readString(path);
    }

    /**
     * Initializes the local JSON database if it doesn't already exist
     *
     * <p>It does not override existing files, to avoid data loss.</p>
     * @throws IOException if the database can't initialize
     */
    public void initialize() throws IOException {
        if (Files.notExists(path)) {
            Files.createDirectories(path.getParent());
            Files.writeString(path, "{\"devices\": [], \"users\": []}");
        }
    }

    /***
     *Parses file into valid JSON
     *
     * @return the root node of the JSON document
     * @throws IOException if the file can't be read
     *                      or contains invalid JSON
     */
    public JsonNode readJson() throws IOException {
        String content = readFile();
        JsonNode root = mapper.readTree(content);
        return root;
    }

    public void writeJson(JsonNode root) throws IOException {
        mapper.writeValue(path.toFile(), root);
    }

    public JsonNode toJsonNode(Object value) {
        return mapper.valueToTree(value);
    }

    public Device toDevice(JsonNode node) throws JsonProcessingException {
        return mapper.treeToValue(node, Device.class);
    }
}
