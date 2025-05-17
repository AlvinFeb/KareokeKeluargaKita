package DAO;

import Utils.DatabaseConnection;
import Utils.PasswordHasher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class UserLoginDAO {
    private Connection connection;

    public UserLoginDAO() {
        this.connection = DatabaseConnection.connectDB();
    }

    // Method to authenticate user
    public String authenticateUser(String usernameOrEmail, String passwordInput) {
        if (connection == null) {
            JOptionPane.showMessageDialog(null, "Database connection error");
            return null;
        }

        try {
            String sql = "SELECT password, role FROM users WHERE (username = ? OR email = ?)";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, usernameOrEmail);
            pst.setString(2, usernameOrEmail);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                String role = rs.getString("role");
                String hashedInputPassword = PasswordHasher.hashPassword(passwordInput);

                if (storedHashedPassword.equals(hashedInputPassword)) {
                    return role; // Return the user's role if authentication succeeds
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        }
        return null; // Return null if authentication fails
    }

    // Method to check if user exists
    public boolean userExists(String username, String email) {
        try {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, email);
            ResultSet rs = pst.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            return false;
        }
    }

    // Method to create new user
    public boolean registerUser(String username, String email, String password, String phone_number) {
        try {
            String hashedPassword = PasswordHasher.hashPassword(password);
            String sql = "INSERT INTO users (username, email, password, phone_number) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, email);
            pst.setString(3, hashedPassword);
            pst.setString(4, phone_number);

            int rowsInserted = pst.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            return false;
        }
    }

    // Close connection when done
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error closing connection: " + e.getMessage());
        }
    }
}