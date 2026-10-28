package com.smartroom.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T> {
    long count();

    boolean save(Map<UUID, T> entities);

    boolean save(T entity);

    Optional<T> findById(UUID id);

    List<T> findAll();

    boolean existsById(UUID id);

    boolean deleteById(UUID id);

    boolean delete(T entity);
}
