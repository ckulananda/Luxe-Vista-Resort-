package com.example.db_luxevista_resort;

public class BookingModel {
    private int bookingID;
    private int roomID;
    private int customerID;
    private String category;
    private double price;
    private String date;

    public BookingModel(int bookingID, int roomID, int customerID, String category, double price, String date) {
        this.bookingID = bookingID;
        this.roomID = roomID;
        this.customerID = customerID;
        this.category = category;
        this.price = price;
        this.date = date;
    }

    public int getBookingID() { return bookingID; }
    public int getRoomID() { return roomID; }
    public int getCustomerID() { return customerID; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getDate() { return date; }
}
