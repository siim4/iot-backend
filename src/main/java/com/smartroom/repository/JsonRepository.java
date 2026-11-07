package com.smartroom.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.smartroom.persistence.JsonDB;

import java.io.IOException;
import java.util.*;

/**
 * Implements shared JSON persistence logic for different entity types.
 *
 * @param <T> the type of entity stored by this repository
 */

public abstract class JsonRepository<T> implements Repository<T> {

    //Protected to allow subclasses to access.
    //TODO: refactor for stronger encapsulation.
    protected final Map<UUID, T> entities;
    protected final JsonDB db;
    private final String arrayName;
    private final Class<T> entityType;

    protected JsonRepository(
            JsonDB db,
            String arrayName,
            Class<T> entityType
    ) {
        this.db = db;
        this.arrayName = arrayName;
        this.entityType = entityType;
        this.entities = new HashMap<>();
    }

    // Models have different accessor method names.
    protected abstract UUID getId(T entity);

    @Override
    public long count() {
        return entities.size();
    }

    @Override
    public Optional<T> findById(UUID id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return entities.containsKey(id);
    }

    /**
     * Loads persisted entities into the in-memory repository.
     *
     * <p>The existing map is replaced only after all entities
     * have been successfully deserialized.
     *
     * @throws IOException if the database cannot be read,
     *                     has an invalid structure, or contains
     *                     invalid entity data
     */

    public void load() throws IOException {

        JsonNode root = db.readJson();
        ArrayNode entityArray = getEntityArray(root);

        Map<UUID, T> loadMap = new HashMap<>();

        for (var node : entityArray) {
            T entity = db.toEntity(node, entityType);

            UUID id = getId(entity);

            if (id == null) {
                throw new IOException("Invalid entity in %s: ID cannot be null.".formatted(arrayName));
            }
            loadMap.put(id, entity);
        }
        entities.clear();
        entities.putAll(loadMap);
    }

    @Override
    public boolean deleteById(UUID id) throws IOException {

        boolean isDeleted = false;

        if (id == null) {
            throw new IllegalArgumentException("Entity ID cannot be null");
        }

        JsonNode root = db.readJson();
        ArrayNode entityArray = getEntityArray(root);

        for (int i = 0; i < entityArray.size(); i++) {
            JsonNode existing = entityArray.get(i);

            if (id.toString().equals(existing.path("id").asText())) {
                entityArray.remove(i);
                isDeleted = true;
                break;
            }
        }
        if (isDeleted) {
            db.writeJson(root);
            entities.remove(id);
        }
        return isDeleted;
    }

    @Override
    public boolean delete(T entity) throws IOException {
        return deleteById(getId(entity));
    }

    @Override
    public boolean save(T entity) throws IOException {
        boolean found = false;
        UUID id = getId(entity);

        if (id == null) {
            throw new IllegalArgumentException("Entity ID cannot be null");
        }

        JsonNode root = db.readJson();
        ArrayNode entityArray = getEntityArray(root);
        JsonNode entityNode = db.toJsonNode(entity);

        for (int i = 0; i < entityArray.size(); i++) {
            JsonNode existing = entityArray.get(i);
            if (id.toString().equals(existing.path("id").asText())) {
                entityArray.set(i, entityNode);
                found = true;
                break;
            }
        }

        if (!found) {
            entityArray.add(entityNode);
        }

        db.writeJson(root);
        entities.put(id, entity);

        return true;
    }

    private ArrayNode getEntityArray(JsonNode root) throws IOException {
        JsonNode node = root.get(arrayName);

        if (!(node instanceof ArrayNode entityArray)) {
            throw new IOException("Invalid database: %s must be an array.".formatted(arrayName));
        }

        return entityArray;
    }
}
