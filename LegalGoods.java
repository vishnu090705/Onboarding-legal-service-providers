package com.example.legislature.models;

public class LegalGoods {

    private String id;           // Firestore document ID
    private String title;        // Example: "IPC Handbook" / "Notary Service"
    private String description;  // Details or law explanation
    private double price;        // 0 = free information
    private String imageUrl;     // Product or info illustration
    private String category;     // "book", "document", "service", "info"

    public LegalGoods() { }

    public LegalGoods(String id, String title, String description, double price, String imageUrl, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
