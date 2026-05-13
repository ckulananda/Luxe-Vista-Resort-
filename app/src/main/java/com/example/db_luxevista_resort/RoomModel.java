package com.example.db_luxevista_resort;

public class RoomModel {
    private int id;
    private String name, imagePath, category, description;
    private double price;
    private int status;

    public RoomModel(int id, String name, String imagePath, String category, String description, double price, int status) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.category = category;
        this.description = description;
        this.price = price;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getImagePath() { return imagePath; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getStatus() { return status; }
}
