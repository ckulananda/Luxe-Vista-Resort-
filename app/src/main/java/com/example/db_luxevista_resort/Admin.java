package com.example.db_luxevista_resort;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Admin extends AppCompatActivity {

    private TextView welcomeText;
    private Button btnManageUsers, btnViewReports, btnLogout, btnAddRoom,btnManageRooms,btnAddService,btnViewResavation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        welcomeText = findViewById(R.id.welcomeAdmin);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnViewReports = findViewById(R.id.btnViewReports);
        btnLogout = findViewById(R.id.btnLogout);
        btnAddRoom = findViewById(R.id.btnAddRoom);
        btnManageRooms=findViewById(R.id.btnManageRooms);
        welcomeText.setText("Welcome, " + GlobalVar.nameU);
        btnAddService=findViewById(R.id.btnAddService);
        btnViewResavation=findViewById(R.id.btnViewReservation);

       btnManageUsers.setOnClickListener(v -> {
            startActivity(new Intent(Admin.this, ManageUsersActivity.class));
        });

        btnViewReports.setOnClickListener(v -> {

            startActivity(new Intent(Admin.this, ViewBookingsActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            GlobalVar.nameU = "User";
            startActivity(new Intent(Admin.this, MainActivity.class));
            finish();
        });
        //Addroom Activity
        btnAddRoom.setOnClickListener(v -> {
            startActivity(new Intent(Admin.this, AddRoomActivity.class));
        });

        btnManageRooms.setOnClickListener(v -> {
            startActivity(new Intent(Admin.this, AdminManageRoomsActivity.class));
        });

        btnAddService.setOnClickListener(view -> {
            startActivity(new Intent(Admin.this,AddServiceActivity.class));
        });

        btnViewResavation.setOnClickListener(view -> {
            startActivity(new Intent(Admin.this,MyReservationActivity.class));

        });



    }
}
