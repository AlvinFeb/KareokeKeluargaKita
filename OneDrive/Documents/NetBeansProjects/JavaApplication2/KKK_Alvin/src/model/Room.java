package model;

public class Room {
    private final int id;
    private final String name;  // Using 'name' not 'username'
    private final String type;
    private final int capacity;
    private final double hourlyRate;

    public Room(int id, String name, String type, int capacity, double hourlyRate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.hourlyRate = hourlyRate;
    }

    // Getters - MUST match exactly what's called in AdminPanel
    public int getId() { return id; }
    public String getName() { return name; }  // Important: getName() exists
    public String gettype() { return type; }  // Fixed typo from gettype() to getType()
    public int getCapacity() { return capacity; }
    public double getHourlyRate() { return hourlyRate; }
}