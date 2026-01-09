package com.example.calculator.model;

public class UnblockRequest {
    private final int id;
    private final int userId;
    private final String username;
    private final String message;
    private final String timestamp;

    public UnblockRequest(int id, int userId, String username, String message, String timestamp) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
}
