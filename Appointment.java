package com.example.legislature.models;

public class Appointment {

    private String id;          // Firestore document ID (not stored, just used)
    private String userId;
    private String providerId;
    private String date;
    private String time;
    private String status;      // pending, accepted, declined

    public Appointment() {
        // Empty constructor required for Firestore
    }

    public Appointment(String userId, String providerId, String date, String time, String status) {
        this.userId = userId;
        this.providerId = providerId;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    // Getter and Setter for id (Firestore doc ID)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
