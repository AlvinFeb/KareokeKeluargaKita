package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Booking {
    private final int id;
    private final int userId;
    private final int roomId;
    private final String roomName;
    private final Date checkIn;
    private final Date checkOut;
    private final String status;
    private final Timestamp createdAt;
    private final double hourlyRate;

    public Booking(int id, int userId, int roomId, String roomName, 
                  Date checkIn, Date checkOut, String status, 
                  Timestamp createdAt, double hourlyRate) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.roomName = roomName;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
        this.createdAt = createdAt;
        this.hourlyRate = hourlyRate;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getRoomId() { return roomId; }
    public String getRoomName() { return roomName; }
    public Date getCheckIn() { return checkIn; }
    public Date getCheckOut() { return checkOut; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public double getHourlyRate() { return hourlyRate; }
}