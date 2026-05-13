package com.example.db_luxevista_resort;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LuxeDB MyDB;

    private Button Inlog;
    private TextView UserNew;
    private EditText txtemail, txtpassword;

    private CheckBox showPasswordCheck;

    // Header navigation
    private TextView navPackages, navResort, navSignIn, navAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        //show password cast
        showPasswordCheck = findViewById(R.id.showPasswordCheck);

        showPasswordCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                txtpassword.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                txtpassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            txtpassword.setSelection(txtpassword.getText().length()); // Keep cursor at end
        });


        // Initialize DB
        MyDB = new LuxeDB(this);

        // Link Views
        txtemail = findViewById(R.id.emailTxt);
        txtpassword = findViewById(R.id.passwordTxt);
        UserNew = findViewById(R.id.Newuser);
        Inlog = findViewById(R.id.LogInbtn);

        // Link Header Navigation
        navPackages = findViewById(R.id.navPackages);
        navResort = findViewById(R.id.navResort);
        navSignIn = findViewById(R.id.navSignIn);
        navAbout = findViewById(R.id.navAbout);

        // Click events for header
        navPackages.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ViewRoomsActivity.class)));

        navResort.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ResortActivity.class)));

        navSignIn.setOnClickListener(v -> Toast.makeText(this, "Already on Sign In page", Toast.LENGTH_SHORT).show());

        navAbout.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AboutUsActivity.class)));

        // Sign Up Click
        UserNew.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            GlobalVar.userChek = 0;
        });

        // Login Click
        Inlog.setOnClickListener(v -> {
            String email = txtemail.getText().toString().trim().toLowerCase();
            String password = txtpassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Email or password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Admin login check
            if (email.equals("admin") && password.equals("pass")) {
                GlobalVar.nameU = "Admin";
                startActivity(new Intent(MainActivity.this, Admin.class));
                return;
            }

            Cursor returnUser = null;
            try {
                returnUser = MyDB.getUser(email, password);

                if (returnUser != null && returnUser.moveToFirst()) {
                    GlobalVar.userID = returnUser.getInt(returnUser.getColumnIndexOrThrow(LuxeDB.USER_ID));
                    GlobalVar.nameU = email;
                    GlobalVar.fName = returnUser.getString(returnUser.getColumnIndexOrThrow(LuxeDB.USER_FNAME));
                    GlobalVar.lName = returnUser.getString(returnUser.getColumnIndexOrThrow(LuxeDB.USER_LNAME));
                    GlobalVar.profileUri = returnUser.getString(returnUser.getColumnIndexOrThrow(LuxeDB.USER_PROFILE_PATH));

                    startActivity(new Intent(MainActivity.this, Customer.class));
                } else {
                    Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            } finally {
                if (returnUser != null) {
                    returnUser.close();
                }
            }
        });
    }
}
