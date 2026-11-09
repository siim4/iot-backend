package com.smartroom.repository;

import com.smartroom.model.Mode;
import com.smartroom.persistence.RelationalDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JdbcModeRepository implements Repository<Mode> {

    private final RelationalDB rdb;

    public JdbcModeRepository(RelationalDB rdb) {
        this.rdb = rdb;
    }

    @Override
    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM modes";

        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getLong(1);
        }
    }

    private Mode mapRow(ResultSet result) throws SQLException {
        return new Mode(
                result.getObject("id", UUID.class),
                result.getObject("owner_id", UUID.class),
                result.getString("name")
        );
    }

    @Override
    public boolean save(Mode entity) throws SQLException {
        String sql = """
                INSERT INTO modes (id, owner_id, name)
                VALUES (?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    owner_id = EXCLUDED.owner_id,
                    name = EXCLUDED.name
                """;

        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, entity.id());
            statement.setObject(2, entity.ownerId());
            statement.setString(3, entity.name());

            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Mode> findById(UUID id) throws SQLException {
        String sql = """
                SELECT id, owner_id, name
                FROM modes
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
    public List<Mode> findAll() throws SQLException {
        String sql = """
                SELECT id, owner_id, name
                FROM modes
                """;
        List<Mode> modes = new ArrayList<>();
        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Mode mode = mapRow(result);
                    modes.add(mode);
                }
            }
        }
        return modes;
    }

    @Override
    public boolean existsById(UUID id) throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM modes WHERE id = ?)";

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
        String sql = "DELETE FROM modes WHERE id = ?";

        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Mode entity) throws SQLException {
        return deleteById(entity.id());
    }

    public List<Mode> findByOwnerId(UUID ownerId) throws SQLException {
        String sql = """
                SELECT id, owner_id, name
                FROM modes
                WHERE owner_id = ?
                """;

        List<Mode> modes = new ArrayList<>();

        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, ownerId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Mode mode = mapRow(result);
                    modes.add(mode);
                }
            }
        }
        return modes;
    }


}
