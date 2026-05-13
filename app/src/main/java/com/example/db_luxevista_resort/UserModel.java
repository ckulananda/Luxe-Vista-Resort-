package com.example.db_luxevista_resort;

public class UserModel {
    public int id;
    public String username;
    public String email;
    public String password;
    public String profilePath;
    public String fname;
    public String lname;
    public String address;
    public String contact;

    // Full constructor
    public UserModel(int id,
                     String username,
                     String email,
                     String profilePath,
                     String fname,
                     String lname,
                     String address,
                     String contact) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profilePath = profilePath;
        this.fname = fname;
        this.lname = lname;
        this.address = address;
        this.contact = contact;
    }


    public UserModel(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
