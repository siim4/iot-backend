package com.smartroom.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T> {
    long count();

    boolean save(T entity) throws Exception;

    Optional<T> findById(UUID id) throws Exception;

    List<T> findAll() throws Exception;

    boolean existsById(UUID id) throws Exception;

    boolean deleteById(UUID id) throws Exception;

    boolean delete(T entity) throws Exception;
}
