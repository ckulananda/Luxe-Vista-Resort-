package com.example.db_luxevista_resort;

import android.net.Uri;

import java.io.Serializable;

public class UserSession implements Serializable {
    public String username = "";
    public String email = "";
    public String password = "";
    public String retypePassword = "";
    public String firstName = "";
    public String lastName = "";
    public String contact = "";
    public String address = "";
    public Uri profileImageUri; // store profile image URI
}