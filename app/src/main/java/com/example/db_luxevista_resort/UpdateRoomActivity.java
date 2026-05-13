package com.example.db_luxevista_resort;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class UpdateRoomActivity extends AppCompatActivity {

    private EditText etRoomName, etRoomCategory, etRoomDescription, etRoomPrice;
    private Spinner spRoomStatus;
    private ImageView imgRoom;
    private Button btnUpdate, btnSelectImage;

    private LuxeDB db;
    private int roomID;
    private String currentImagePath; // store old image path
    private static final int PICK_IMAGE_REQUEST = 101;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_room);

        etRoomName = findViewById(R.id.etRoomName);
        etRoomCategory = findViewById(R.id.etRoomCategory);
        etRoomDescription = findViewById(R.id.etRoomDescription);
        etRoomPrice = findViewById(R.id.etRoomPrice);
        spRoomStatus = findViewById(R.id.spRoomStatus);
        imgRoom = findViewById(R.id.imgRoom);
        btnUpdate = findViewById(R.id.btnUpdateRoom);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        db = new LuxeDB(this);

        // Populate Spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Unavailable", "Available"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoomStatus.setAdapter(statusAdapter);

        // Get Intent data
        roomID = getIntent().getIntExtra("roomID", 0);
        String name = getIntent().getStringExtra("roomName");
        currentImagePath = getIntent().getStringExtra("roomImagePath");
        String category = getIntent().getStringExtra("roomCategory");
        String description = getIntent().getStringExtra("roomDescription");
        double price = getIntent().getDoubleExtra("roomPrice", 0);
        int status = getIntent().getIntExtra("roomStatus", 0);

        // Populate views
        etRoomName.setText(name);
        etRoomCategory.setText(category);
        etRoomDescription.setText(description);
        etRoomPrice.setText(String.valueOf(price));
        spRoomStatus.setSelection(status); // 0 = Unavailable, 1 = Available

        if (currentImagePath != null && !currentImagePath.isEmpty()) {
            Bitmap bitmap = FileUtils.decodeFileToBitmap(currentImagePath);
            if (bitmap != null) imgRoom.setImageBitmap(bitmap);
        }

        // Select new image
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Update room
        btnUpdate.setOnClickListener(v -> {
            String newName = etRoomName.getText().toString().trim();
            String newCategory = etRoomCategory.getText().toString().trim();
            String newDescription = etRoomDescription.getText().toString().trim();
            String priceStr = etRoomPrice.getText().toString().trim();
            double newPrice = priceStr.isEmpty() ? 0 : Double.parseDouble(priceStr);
            int newStatus = spRoomStatus.getSelectedItemPosition();

            String imagePathToSave = currentImagePath; // keep old image if not selected
            if (selectedImageUri != null) {
                // Save selected image to app directory
                File imageFile = new File(FileUtils.copyUriToFile(this, selectedImageUri, "room_images"));
                if (imageFile != null) imagePathToSave = imageFile.getAbsolutePath();
            }

            boolean updated = db.updateRoom(roomID, newName, imagePathToSave, newCategory,
                    newDescription, newPrice, newStatus);

            if (updated) {
                Toast.makeText(UpdateRoomActivity.this, "Room updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(UpdateRoomActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = FileUtils.decodeFileToBitmap(FileUtils.getPathFromUri(this, selectedImageUri));
                if (bitmap != null) imgRoom.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
