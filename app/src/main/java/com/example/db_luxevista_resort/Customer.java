package com.example.db_luxevista_resort;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.InputStream;





public class Customer extends AppCompatActivity {

    private Button logoutBtn,btnBookRoom,btnMyBookings,btnResavation,btnMyResevation;

    private ShapeableImageView profilePic;
    private TextView welcomeCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        profilePic = findViewById(R.id.profilePic);
        welcomeCustomer = findViewById(R.id.welcomeCustomer);
        logoutBtn = findViewById(R.id.btnLogout);
        btnBookRoom = findViewById(R.id.btnBookRoom);
        btnMyBookings=findViewById(R.id.btnMyBookings);
        btnResavation=findViewById(R.id.btnReservation);
        btnMyResevation=findViewById(R.id.btnMyresevation);


        logoutBtn.setOnClickListener(v -> {
            // 1. Clear user session / GlobalVar
            GlobalVar.userID = -1;
            GlobalVar.nameU = "User";
            GlobalVar.fName = "";
            GlobalVar.lName = "";
            GlobalVar.profileUri = "";

            // 2. Go back to login screen
            Intent intent = new Intent(Customer.this, MainActivity.class);
            startActivity(intent);

            // 3. Finish current activity so back button won't return here
            finish();
        });

        AddBookingActivity addBookingActivity;
        btnBookRoom.setOnClickListener(v -> {
            Intent intent = new Intent(Customer.this, AddBookingActivity.class);

            // Pass global user values
            intent.putExtra("userID", GlobalVar.userID);
            intent.putExtra("userName", GlobalVar.fName + " " + GlobalVar.lName);

            // ⚠️ IMPORTANT: You must also decide which room to book.
            // Example: If you clicked a specific room, pass its ID like this:
            // intent.putExtra("roomID", selectedRoomId);

            startActivity(intent);
        });

        ViewBookingsActivity viewBookingsActivity;
        btnMyBookings.setOnClickListener(v -> {
            Intent intent = new Intent(Customer.this,ViewBookingsActivity.class);

            // Pass global user values
            intent.putExtra("userID", GlobalVar.userID);
            intent.putExtra("userName", GlobalVar.fName + " " + GlobalVar.lName);

            // ⚠️ IMPORTANT: You must also decide which room to book.
            // Example: If you clicked a specific room, pass its ID like this:
            // intent.putExtra("roomID", selectedRoomId);

            startActivity(intent);
        });

        //resevation
        AddReservationActivity addReservationActivity;
        btnResavation.setOnClickListener(view -> {
                    Intent intent = new Intent(Customer.this,AddReservationActivity.class);

                    intent.putExtra("userID", GlobalVar.userID);
                    intent.putExtra("userName", GlobalVar.fName + " " + GlobalVar.lName);

                    startActivity(intent);
                }
                );


        MyReservationActivity myReservationActivity;
        btnMyResevation.setOnClickListener(view -> {

            Intent intent = new Intent(Customer.this,MyReservationActivity.class);

            intent.putExtra("userID", GlobalVar.userID);
            intent.putExtra("userName", GlobalVar.fName + " " + GlobalVar.lName);

            startActivity(intent);

        });






        // Show user's name
        welcomeCustomer.setText("Welcome, " + GlobalVar.fName + " " + GlobalVar.lName);

        // Load profile picture from DB / GlobalVar
        loadProfileImage(GlobalVar.profileUri);
    }

    private void loadProfileImage(String uriString) {
        if (uriString == null || uriString.isEmpty()) {
            profilePic.setImageResource(R.mipmap.ic_launcher_round);
            return;
        }

        try {
            Uri uri = Uri.parse(uriString);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                profilePic.setImageBitmap(bitmap);
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            profilePic.setImageResource(R.mipmap.ic_launcher_round);
        }
    }
}
