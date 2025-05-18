package DAO;

import Utils.DatabaseConnection;
import model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import java.util.HashSet;


public class RoomDAO {
    private Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/kkk_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public RoomDAO() {
        this.connection = DatabaseConnection.connectDB();
    }

    public Room getRoomById(int id) {
        String sql = "SELECT id, name, type, capacity, hourly_rate FROM rooms WHERE id = ?";
        try (PreparedStatement pst = getValidConnection().prepareStatement(sql)) {
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
            PreparedStatement pst = getValidConnection().prepareStatement(sql);
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
            Statement st = getValidConnection().createStatement();
            ResultSet rs = st.executeQuery("SELECT id, name, type, capacity, hourly_rate, booked_hours FROM rooms");
            
            while (rs.next()) {
                Room room = new Room(
                    rs.getInt("id"),
                    rs.getString("name"), 
                    rs.getString("type"),
                    rs.getInt("capacity"),
                    rs.getDouble("hourly_rate")
                );
                room.setBookedHours(rs.getString("booked_hours"));
                rooms.add(room);
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
            PreparedStatement pst = getValidConnection().prepareStatement(sql);
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
            PreparedStatement pst = getValidConnection().prepareStatement("DELETE FROM rooms WHERE id = ?");
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
        
        try {
            Statement stmt = getValidConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
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
        String sql = "SELECT * FROM rooms WHERE type = ? AND capacity >= ? AND hourly_rate <= ?";
        
        try {
            PreparedStatement stmt = getValidConnection().prepareStatement(sql);
            
            stmt.setString(1, type.toLowerCase());
            stmt.setInt(2, minCapacity);
            stmt.setDouble(3, maxPrice);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room room = extractRoomFromResultSet(rs);
                rooms.add(room);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error searching rooms: " + e.getMessage());
            e.printStackTrace();
        }
        return rooms;
    }

    public boolean bookRoom(String roomName, int startHour, int duration) throws SQLException {
        Connection conn = getValidConnection();
        // 1. Check available hours
        String bookedHours = getBookedHours(conn, roomName);
        Set<Integer> unavailableHours = parseBookedHours(bookedHours);
        
        // 2. Validate requested time slot
        for (int i = startHour; i < startHour + duration; i++) {
            int hour = i % 24; // Handle overflow
            if (unavailableHours.contains(hour)) {
                return false; // Hour already booked
            }
        }
        
        // 3. Update database
        String newBookedHours = updateBookedHours(bookedHours, startHour, duration);
        String sql = "UPDATE rooms SET booked_hours = ? WHERE name = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newBookedHours);
            stmt.setString(2, roomName);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private String getBookedHours(Connection conn, String roomName) throws SQLException {
        String sql = "SELECT booked_hours FROM rooms WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomName);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("booked_hours") : null;
        }
    }

    private Set<Integer> parseBookedHours(String bookedHours) {
        Set<Integer> hours = new HashSet<>();
        if (bookedHours != null && !bookedHours.isEmpty()) {
            for (String hour : bookedHours.split(",")) {
                try {
                    hours.add(Integer.valueOf(hour.trim()));
                } catch (NumberFormatException e) {
                    // Skip invalid entries
                }
            }
        }
        return hours;
    }

    private String updateBookedHours(String current, int start, int duration) {
        Set<Integer> hours = parseBookedHours(current);
        for (int i = start; i < start + duration; i++) {
            hours.add(i % 24);
        }
        return hours.stream().map(String::valueOf).collect(Collectors.joining(","));
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
            Room room = new Room(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getInt("capacity"),
                rs.getDouble("hourly_rate")
            );
            
            // Get booked_hours if available
            try {
                String bookedHours = rs.getString("booked_hours");
                room.setBookedHours(bookedHours);
            } catch (SQLException e) {
                // Column might not exist in all queries, ignore
            }
            
            return room;
        } catch (SQLException e) {
            System.err.println("Error extracting room from result set: " + e.getMessage());
            throw new RuntimeException("Failed to extract room data from database", e);
        }
    }

    public void close() {
        closeConnection();    
    }

    private Connection getConnection() throws SQLException {
        return getValidConnection();
    }
}
