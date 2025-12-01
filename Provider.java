package com.example.legislature.models;

public class Provider {

    private String id;
    private String name;
    private String specialization;
    private String phone;
    private String email;
    private long credits;
    private String lastLoginDate;
    private String role;

    public Provider() { }

    public Provider(String id, String name, String specialization, String phone, String email) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;

        this.credits = 0;
        this.lastLoginDate = "";
        this.role = "provider";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public long getCredits() { return credits; }       
    public void setCredits(long credits) { this.credits = credits; }

    public String getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(String lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
