package com.smartroom.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.smartroom.model.Device;
import com.smartroom.persistence.JsonDB;

import java.io.IOException;
import java.util.*;

public class JsonDeviceRepository implements Repository<Device> {

    private final Map<UUID, Device> devices;
    private final JsonDB db;

    public JsonDeviceRepository(JsonDB db) {
        this.db = db;
        this.devices = new HashMap<>();
    }

    public Optional<Device> findById(UUID id) {
        return Optional.ofNullable(devices.get(id));
    }

    @Override
    public long count() {
        return devices.size();
    }

    @Override
    public boolean save(Device device) throws IOException {
        boolean found = false;
        JsonNode root = db.readJson();
        ArrayNode deviceArray = (ArrayNode) root.get("devices");
        JsonNode deviceNode = db.toJsonNode(device);

        for (int i = 0; i < deviceArray.size(); i++) {
            JsonNode existing = deviceArray.get(i);

            if (device.getID().toString().equals(existing.path("id").asText())) {
                deviceArray.set(i, deviceNode);
                found = true;
                break;
            }
        }
        if (!found) {
            deviceArray.add(deviceNode);
        }
        // If writeJson fails, execution stops before updating the map.
        db.writeJson(root);
        devices.put(device.getID(), device);
        return true;
    }

    @Override
    public List<Device> findAll() {
        return new ArrayList<>(devices.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return devices.containsKey(id);
    }

    @Override
    public boolean deleteById(UUID id) throws IOException {
        boolean isDeleted = false;
        JsonNode root = db.readJson();
        ArrayNode deviceArray = (ArrayNode) root.get("devices");

        for (int i = 0; i < deviceArray.size(); i++) {
            JsonNode existing = deviceArray.get(i);
            if (id.toString().equals(existing.path("id").asText())) {
                deviceArray.remove(i);
                isDeleted = true;
                break;
            }
        }


        if (isDeleted) {
            db.writeJson(root);
            devices.remove(id);
        }
        return isDeleted;
    }

    @Override
    public boolean delete(Device device) throws IOException {
        return deleteById(device.getID());
    }

    public void showAllDevices() {
        for (var device : devices.entrySet()) {
            System.out.println(device.getValue());
            System.out.println();
        }
    }

    /**
     * Loads persisted devices into the in-memory repository.
     *
     * <p>The existing map is replaced only after all devices
     * have been successfully deserialized.
     *
     * @throws IOException if the database cannot be read,
     *                     has an invalid structure, or contains
     *                     invalid device data
     */
    public void load() throws IOException {
        JsonNode root = db.readJson();
        JsonNode deviceArray = root.get("devices");

        if (deviceArray == null || !deviceArray.isArray()) {
            throw new IOException("Invalid database.");
        }

        Map<UUID, Device> loadArr = new HashMap<>();

        for (JsonNode node : deviceArray) {
            Device device = db.toDevice(node);
            loadArr.put(device.getID(), device);
        }

        // We replace the map only after everything succeeds.
        devices.clear();
        devices.putAll(loadArr);
    }
}
