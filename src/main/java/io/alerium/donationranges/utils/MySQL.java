package io.alerium.donationranges.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL {

    private final String hostname;
    private final String username;
    private final String password;
    private final String database;
    private final int port;

    private HikariDataSource dataSource;

    public MySQL(String hostname, String username, String password, String database, int port) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.database = database;
        this.port = port;
    }

    public void connect() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);

        dataSource = new HikariDataSource(config);

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS donations_data (uuid VARCHAR(36) NOT NULL PRIMARY KEY, donation_amount DOUBLE NOT NULL);");
        ) {
            statement.execute();
        }
    }

    public void disconnect() {
        dataSource.close();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
