package DAO;

import Utils.DatabaseConnection;
import model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class RoomDAO {
    private Connection connection;

    public RoomDAO() {
        this.connection = DatabaseConnection.connectDB();
    }

    // Create operation
    public boolean addRoom(String username, String type, int capacity, double hourlyRate) {
        try {
            String sql = "INSERT INTO rooms (username, type, capacity, hourly_rate) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, type);  
            pst.setInt(3, capacity);
            pst.setDouble(4, hourlyRate);

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding room: " + e.getMessage());
            return false;
        }
    }

    // Read operation - Get all rooms
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, username, type, capacity, hourly_rate FROM rooms");
            
            while (rs.next()) {
                rooms.add(new Room(
                    rs.getInt("id"),
                    rs.getString("username"), 
                    rs.getString("type"),
                    rs.getInt("capacity"),
                    rs.getDouble("hourly_rate")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading rooms: " + e.getMessage());
        }
        return rooms;
    }

    // Update operation
    public boolean updateRoom(Room room) {
        try {
            String sql = "UPDATE rooms SET username = ?, type = ?, capacity = ?, hourly_rate = ? WHERE id = ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, room.getUsername());  
            pst.setString(2, room.gettype());
            pst.setInt(3, room.getCapacity());
            pst.setDouble(4, room.getHourlyRate());
            pst.setInt(5, room.getId());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating room: " + e.getMessage());
            return false;
        }
    }

    // Delete operation remains the same
    public boolean deleteRoom(int id) {
        try {
            PreparedStatement pst = connection.prepareStatement("DELETE FROM rooms WHERE id = ?");
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting room: " + e.getMessage());
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error closing connection: " + e.getMessage());
        }
    }

    public Room getRoomById(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}