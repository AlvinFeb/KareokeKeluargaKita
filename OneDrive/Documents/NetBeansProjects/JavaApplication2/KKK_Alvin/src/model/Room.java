package model;

public class Room {
    private final int id;
    private final String username;
    private final String type;  
    private final int capacity;
    private final double hourlyRate;

    public Room(int id, String username, String type, int capacity, double hourlyRate) {
        this.id = id;
        this.username = username;
        this.type = type;
        this.capacity = capacity;
        this.hourlyRate = hourlyRate;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String gettype() { return type; }  
    public int getCapacity() { return capacity; }
    public double getHourlyRate() { return hourlyRate; }

    public Object getName() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}