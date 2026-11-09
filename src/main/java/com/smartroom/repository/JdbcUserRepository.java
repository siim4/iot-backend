package com.smartroom.repository;

import com.smartroom.model.User;
import com.smartroom.persistence.RelationalDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JdbcUserRepository implements Repository<User> {

    private final RelationalDB rdb;

    public JdbcUserRepository(RelationalDB rdb) {
        this.rdb = rdb;
    }

    @Override
    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";

        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getLong(1);
        }
    }

    @Override
    public boolean save(User entity) throws SQLException {
        String sql = """
                INSERT INTO users (id, username, password_hash)
                VALUES (?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                username = EXCLUDED.username,
                password_hash = EXCLUDED.password_hash
                """;
        try (Connection connection = rdb.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setObject(1, entity.id());
                statement.setString(2, entity.username());
                statement.setString(3, entity.passwordHash());

                return statement.executeUpdate() > 0;
            }
        }
    }

    private User mapRow(ResultSet result) throws SQLException {
        UUID userId = result.getObject("id", UUID.class);
        String username = result.getString("username");
        String passwordHash = result.getString("password_hash");

        return new User(userId, username, passwordHash);
    }

    @Override
    public Optional<User> findById(UUID id) throws SQLException {
        String sql = """
                SELECT id, username, password_hash
                FROM users
                WHERE id = ?
                """;
        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(result));
            }
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        String sql = """
                SELECT id, username, password_hash
                FROM users
                """;
        List<User> users = new ArrayList<>();

        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    User user = mapRow(result);
                    users.add(user);
                }
            }
        }
        return users;
    }

    @Override
    public boolean existsById(UUID id) throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM users WHERE id = ?)";

        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);

            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getBoolean(1);
            }
        }
    }

    @Override
    public boolean deleteById(UUID id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(User entity) throws SQLException {
        return deleteById(entity.id());
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = """
                SELECT id, username, password_hash
                FROM users
                WHERE username = ?
                """;
        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(result));
            }
        }
    }
}
