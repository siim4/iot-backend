package com.smartroom.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class RelationalDB implements AutoCloseable {

    private final HikariDataSource dataSource;

    public RelationalDB(String url, String username, String password) {

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        // Max connections, active and idle.
        config.setMaximumPoolSize(5);
        // HikariCP target to keep at least one idle connection available.
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10000);

        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // We implement AutoCloseable.close to close the connection pool resource.
    // We call this implementing a shutdown hook on the application.
    @Override
    public void close() {
        dataSource.close();
    }
}
