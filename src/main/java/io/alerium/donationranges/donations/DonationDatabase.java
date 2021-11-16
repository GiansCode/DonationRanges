package io.alerium.donationranges.donations;

import io.alerium.donationranges.utils.MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DonationDatabase {

    private final MySQL mySQL;

    public DonationDatabase(MySQL mySQL) {
        this.mySQL = mySQL;
    }

    public double getPlayerDonationAmount(UUID uuid) throws SQLException {
        try (
                Connection connection = mySQL.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM donations_data WHERE uuid = ?;");
        ) {
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();
            if (!result.next())
                return 0;
            return result.getDouble("donation_amount");
        }
    }

    public void updatePlayerDonationAmount(UUID uuid, double amount) throws SQLException {
        try (
                Connection connection = mySQL.getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO donations_data (uuid, donation_amount) VALUES (?, ?) ON DUPLICATE KEY UPDATE donation_amount = ?;")
        ) {
            statement.setString(1, uuid.toString());
            statement.setDouble(2, amount);
            statement.setDouble(3, amount);

            statement.execute();
        }
    }

}
