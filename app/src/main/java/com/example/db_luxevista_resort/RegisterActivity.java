package com.example.db_luxevista_resort;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {

    private LuxeDB MyDB;

    private ImageView profileImage;
    private EditText usernameTxt, emailTxt, passwordTxt, fnameTxt, lnameTxt, contactTxt, addressTxt;
    private Button registerBtn;

    private Uri selectedImageUri = null;

    // -------------------- PICK IMAGE --------------------
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;

                    // Persist read permission for SAF URIs
                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Show the image immediately
                    loadProfileImage(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        MyDB = new LuxeDB(this);

        // Link Views
        profileImage = findViewById(R.id.profileImage);
        usernameTxt = findViewById(R.id.usernameTxt);
        emailTxt = findViewById(R.id.emailTxt);
        passwordTxt = findViewById(R.id.passwordTxt);
        fnameTxt = findViewById(R.id.fnameTxt);
        lnameTxt = findViewById(R.id.lnameTxt);
        contactTxt = findViewById(R.id.contactTxt);
        addressTxt = findViewById(R.id.addressTxt);
        registerBtn = findViewById(R.id.registerBtn);

        // Profile image click
        profileImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // Register button click
        registerBtn.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = usernameTxt.getText().toString().trim();
        String email = emailTxt.getText().toString().trim().toLowerCase();
        String password = passwordTxt.getText().toString().trim();
        String fname = fnameTxt.getText().toString().trim();
        String lname = lnameTxt.getText().toString().trim();
        String contact = contactTxt.getText().toString().trim();
        String address = addressTxt.getText().toString().trim();

//E mail Validation
        if (!email.contains("@")) {
            Toast.makeText(this, "Invalid email address. Must contain @", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || fname.isEmpty() ||
                lname.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String profilePath = selectedImageUri != null ? selectedImageUri.toString() : "";

        boolean inserted = MyDB.insertUser(username, email, password, profilePath, fname, lname, address, contact);
        if (inserted) {
            // Save to GlobalVar for immediate use
            GlobalVar.profileUri = profilePath;
            GlobalVar.fName = fname;
            GlobalVar.lName = lname;

            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Error! Email or username may already exist.", Toast.LENGTH_SHORT).show();
        }
    }

    // -------------------- LOAD IMAGE FROM URI --------------------
    private void loadProfileImage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                profileImage.setImageBitmap(bitmap);
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            profileImage.setImageResource(R.mipmap.ic_launcher_round);
        }
    }
}
