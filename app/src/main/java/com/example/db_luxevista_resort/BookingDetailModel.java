package com.example.db_luxevista_resort;

public class BookingDetailModel {
    private int bookingID;
    private String roomName; // Fetched from tblLuxeRooms
    private double price; // Fetched from tblLuxeRooms
    private String userName; // Fetched from users
    private String bookingCategory;
    private String bookingDate;

    public BookingDetailModel(int bookingID, String roomName, double price, String userName, String bookingCategory, String bookingDate) {
        this.bookingID = bookingID;
        this.roomName = roomName;
        this.price = price;
        this.userName = userName;
        this.bookingCategory = bookingCategory;
        this.bookingDate = bookingDate;
    }

    // Getters for all fields
    public int getBookingID() { return bookingID; }
    public String getRoomName() { return roomName; }
    public double getPrice() { return price; }
    public String getUserName() { return userName; }
    public String getBookingCategory() { return bookingCategory; }
    public String getBookingDate() { return bookingDate; }
}