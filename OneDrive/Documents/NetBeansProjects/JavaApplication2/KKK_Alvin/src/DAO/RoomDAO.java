package DAO;

import Utils.DatabaseConnection;
import model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class RoomDAO {
    private Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database_name";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    public RoomDAO() {
        this.connection = DatabaseConnection.connectDB();
    }

    public Room getRoomById(int id) {
        String sql = "SELECT id, name, type, capacity, hourly_rate FROM rooms WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
        
            if (rs.next()) {
                return new Room(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getInt("capacity"),
                    rs.getDouble("hourly_rate")
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching room: " + e.getMessage());
        }
        return null; // Return null if no room found
    }
   
    public boolean addRoom(String name, String type, int capacity, double hourlyRate) {
        try {
            String sql = "INSERT INTO rooms (name, type, capacity, hourly_rate) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, name);
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
            ResultSet rs = st.executeQuery("SELECT id, name, type, capacity, hourly_rate FROM rooms");
            
            while (rs.next()) {
                rooms.add(new Room(
                    rs.getInt("id"),
                    rs.getString("name"), 
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
            String sql = "UPDATE rooms SET name = ?, type = ?, capacity = ?, hourly_rate = ? WHERE id = ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, room.getName());  
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
    
    private Connection getValidConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initializeConnection();
        }
        return connection;
    }
    
    public List<Room> getAllAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE is_booked = false";
        
        try (Connection conn = getValidConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public List<Room> searchRooms(String type, int minCapacity, double maxPrice) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE type = ? AND capacity >= ? AND hourly_rate <= ? AND is_booked = false";
        
        try (Connection conn = getValidConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type.toLowerCase());
            stmt.setInt(2, minCapacity);
            stmt.setDouble(3, maxPrice);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(extractRoomFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public boolean bookRoom(String roomName, String bookingDate, int hours) {
        String sql = "UPDATE rooms SET is_booked = true, booking_date = ?, booking_hours = ? WHERE name = ?";
    
        try (Connection conn = getValidConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
        
            // Fixed: using bookingDate parameter instead of undefined date variable
            stmt.setString(1, bookingDate);
            stmt.setInt(2, hours);
            stmt.setString(3, roomName);
        
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Better error handling than printStackTrace()
            System.err.println("Error booking room: " + e.getMessage());
            // Or log using a proper logging framework
            // logger.error("Error booking room", e);
            return false;
        }
    }

    private void initializeConnection() {
        try {
        Class.forName("com.mysql.cj.jdbc.Driver");
            
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Database connection established");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database: " + e.getMessage(), e);
        }
    }
    

    private Room extractRoomFromResultSet(ResultSet rs) {
    try {
        return new Room(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("type"),
            rs.getInt("capacity"),
            rs.getDouble("hourly_rate")
        );
    } catch (SQLException e) {

        System.err.println("Error extracting room from result set: " + e.getMessage());

        throw new RuntimeException("Failed to extract room data from database", e);

    }
}

    public void close() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
