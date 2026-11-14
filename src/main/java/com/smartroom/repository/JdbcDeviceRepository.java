package com.smartroom.repository;

import com.smartroom.model.Device;
import com.smartroom.persistence.RelationalDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JdbcDeviceRepository implements DeviceRepository {
    private final RelationalDB rdb;

    public JdbcDeviceRepository(RelationalDB rdb) {
        this.rdb = rdb;
    }

    @Override
    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM devices";

        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getLong(1);
        }
    }

    /*
    For consistency with JSON repository and to simplify operations
    we'll have this method implement update or insert (upsert)
    depending on if ID already exists.
     */
    @Override
    public boolean save(Device entity) throws SQLException {
        String sql = """
                INSERT INTO devices (id, owner_id, name, type, online, powered_on)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                owner_id = EXCLUDED.owner_id,
                name = EXCLUDED.name,
                type = EXCLUDED.type,
                online = EXCLUDED.online,
                powered_on = EXCLUDED.powered_on
                """;
        try (Connection connection = rdb.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Replace each placeholder with values from the device's getters.
                statement.setObject(1, entity.getId());
                statement.setObject(2, entity.getOwnerId());
                statement.setString(3, entity.getName());
                statement.setString(4, entity.getType().name());
                statement.setBoolean(5, entity.isOnline());
                statement.setBoolean(6, entity.getPoweredOn());

                return statement.executeUpdate() > 0;
            }
        }
    }

    @Override
    public Optional<Device> findById(UUID id) throws SQLException {

        String sql = """
                SELECT id, owner_id, name, type, online, powered_on
                FROM  devices
                WHERE id = ?
                """;
        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }

                Device device = mapRow(result);

                return Optional.of(device);
            }
        }
    }

    /*
    Unlike our handling of JSON with Jackson library, we have to implement
    mapping ourselves. For this purpose and as not to repeat code,
    we've extracted that logic to this private helper method.
     */
    private Device mapRow(ResultSet result) throws SQLException {
        UUID deviceId = result.getObject("id", UUID.class);
        UUID ownerId = result.getObject("owner_id", UUID.class);
        String name = result.getString("name");
        Device.Type type = Device.Type.valueOf(result.getString("type"));
        boolean online = result.getBoolean("online");
        boolean poweredOn = result.getBoolean("powered_on");

        return new Device(
                deviceId,
                ownerId,
                name,
                type,
                online,
                poweredOn
        );
    }

    @Override
    public List<Device> findAll() throws SQLException {
        String sql = """
                SELECT id, owner_id, name, type, online, powered_on
                FROM devices
                """;
        List<Device> devices = new ArrayList<>();

        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Device device = mapRow(result);
                    devices.add(device);
                }
            }
        }
        return devices;
    }

    @Override
    public boolean existsById(UUID id) throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM devices WHERE id = ?)";

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
        String sql = "DELETE FROM devices WHERE id = ?";

        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);
            // Method executeUpdate, despite its name, works for INSERT, UPDATE and DELETE.
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Device entity) throws SQLException {
        return deleteById(entity.getId());
    }

    public List<Device> findByOwnerId(UUID ownerId) throws SQLException {
        String sql = """
                SELECT id, owner_id, name, type, online, powered_on
                FROM devices
                WHERE owner_id = ?
                """;
        List<Device> devices = new ArrayList<>();

        try (Connection connection = rdb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, ownerId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Device device = mapRow(result);
                    devices.add(device);
                }
            }
        }
        return devices;
    }
}
