package model;

import java.sql.Timestamp;

public class User {
    private final int id;
    private final String name;
    private final String email;
    private final String phone;
    private final String role;
    private final Timestamp createdAt;

    public User(int id, String name, String email, String phone, String role, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public Timestamp getCreatedAt() { return createdAt; }
}