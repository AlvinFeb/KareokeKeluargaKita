package DAO;

import model.Room;
import model.Booking;
import Utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.connectDB();
    }

    // 1. Browse Available Rooms
    public List<Room> getAvailableRooms(Date checkIn, Date checkOut) {
        List<Room> availableRooms = new ArrayList<>();
        try {
            String sql = "SELECT * FROM rooms WHERE id NOT IN (" +
                         "SELECT room_id FROM bookings " +
                         "WHERE (check_in < ? AND check_out > ?)" +
                         "OR (check_in >= ? AND check_in < ?)" +
                         "OR (check_out > ? AND check_out <= ?))";
            
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setDate(1, checkOut);
            pst.setDate(2, checkIn);
            pst.setDate(3, checkIn);
            pst.setDate(4, checkOut);
            pst.setDate(5, checkIn);
            pst.setDate(6, checkOut);
            
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                availableRooms.add(new Room(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("type"),
                    rs.getInt("capacity"),
                    rs.getDouble("hourly_rate")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error finding available rooms: " + e.getMessage());
        }
        return availableRooms;
    }

    // 2. Book a Room
    public boolean bookRoom(int userId, int roomId, Date checkIn, Date checkOut) {
        try {
            if (checkOut.compareTo(checkIn) <= 0) {
            JOptionPane.showMessageDialog(null, 
                "Check-out must be after check-in", 
                "Invalid Dates", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // 2. Check room availability
        if (!isRoomAvailable(roomId, checkIn, checkOut)) {
            JOptionPane.showMessageDialog(null, 
                "Room not available for selected dates", 
                "Unavailable", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // 3. Create booking
        String sql = "INSERT INTO bookings (user_id, room_id, check_in, check_out) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, userId);
            pst.setInt(2, roomId);
            pst.setDate(3, checkIn);
            pst.setDate(4, checkOut);
            
            int affected = pst.executeUpdate();
            if (affected == 1) {
                JOptionPane.showMessageDialog(null, 
                    "Booking confirmed!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, 
            "Booking failed: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    return false;
    }

    // 3. View User Bookings
    public List<Booking> getUserBookings(int userId) {
        List<Booking> bookings = new ArrayList<>();
        try {
            String sql = "SELECT b.*, r.username as room_name, r.type, r.hourly_rate " +
                         "FROM bookings b JOIN rooms r ON b.room_id = r.id " +
                         "WHERE b.user_id = ? ORDER BY b.check_in DESC";
            
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                bookings.add(new Booking(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("room_id"),
                    rs.getString("room_name"),
                    rs.getDate("check_in"),
                    rs.getDate("check_out"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at"),
                    rs.getDouble("hourly_rate")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading bookings: " + e.getMessage());
        }
        return bookings;
    }

    // 4. Cancel Booking
    public boolean cancelBooking(int bookingId) {
        try {
            String sql = "UPDATE bookings SET status = 'cancelled' WHERE id = ? AND status = 'confirmed'";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, bookingId);
            
            int affected = pst.executeUpdate();
            if (affected == 0) {
                JOptionPane.showMessageDialog(null, "Booking cannot be cancelled");
                return false;
            }
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Cancellation failed: " + e.getMessage());
            return false;
        }
    }

    // Helper method to check room availability
    private boolean isRoomAvailable(int roomId, Date checkIn, Date checkOut) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings " +
                     "WHERE room_id = ? AND status = 'confirmed' " +
                     "AND ((check_in < ? AND check_out > ?) " +
                     "OR (check_in >= ? AND check_in < ?) " +
                     "OR (check_out > ? AND check_out <= ?))";
        
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, roomId);
        pst.setDate(2, checkOut);
        pst.setDate(3, checkIn);
        pst.setDate(4, checkIn);
        pst.setDate(5, checkOut);
        pst.setDate(6, checkIn);
        pst.setDate(7, checkOut);
        
        ResultSet rs = pst.executeQuery();
        return rs.next() && rs.getInt(1) == 0;
    }

    public void closeConnection() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error closing connection: " + e.getMessage());
        }
    }
}