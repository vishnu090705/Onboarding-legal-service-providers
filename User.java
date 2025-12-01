package com.example.legislature.models;

public class User {

    private String uid;
    private String name;
    private String email;
    private String phone;
    private long credits;              // ⭐ Added
    private String lastLoginDate;      // ⭐ Added
    private String role;               // ⭐ Added

    public User() { }

    public User(String uid, String name, String email, String phone) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;

        this.credits = 0;              // default
        this.lastLoginDate = "";       // default
        this.role = "user";            // default
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public long getCredits() { return credits; }      // ⭐ Added
    public void setCredits(long credits) { this.credits = credits; }

    public String getLastLoginDate() { return lastLoginDate; }  // ⭐ Added
    public void setLastLoginDate(String lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    public String getRole() { return role; }          // ⭐ Added
    public void setRole(String role) { this.role = role; }
}
