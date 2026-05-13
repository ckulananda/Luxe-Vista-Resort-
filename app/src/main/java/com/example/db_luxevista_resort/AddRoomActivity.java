package com.example.db_luxevista_resort;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddRoomActivity extends AppCompatActivity {

    private LuxeDB MyDB;

    private EditText roomNameTxt, roomCategoryTxt, roomPriceTxt, roomDescriptionTxt;
    private ImageView roomImage;
    private RadioGroup statusGroup;
    private Button btnSaveRoom;

    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    roomImage.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        MyDB = new LuxeDB(this);

        roomNameTxt = findViewById(R.id.roomNameTxt);
        roomCategoryTxt = findViewById(R.id.roomCategoryTxt);
        roomPriceTxt = findViewById(R.id.roomPriceTxt);
        roomDescriptionTxt = findViewById(R.id.roomDescriptionTxt); // <-- new
        roomImage = findViewById(R.id.roomImage);
        statusGroup = findViewById(R.id.statusGroup);
        btnSaveRoom = findViewById(R.id.btnSaveRoom);

        roomImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnSaveRoom.setOnClickListener(v -> saveRoom());
    }

    private void saveRoom() {
        String name = roomNameTxt.getText().toString().trim();
        String category = roomCategoryTxt.getText().toString().trim();
        String priceStr = roomPriceTxt.getText().toString().trim();
        String description = roomDescriptionTxt.getText().toString().trim(); // <-- new
        int status = (statusGroup.getCheckedRadioButtonId() == R.id.radioAvailable) ? 1 : 0;

        if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty() || description.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "All fields and image are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        // Copy image to internal storage
        String savedImagePath = copyImageToInternalStorage(selectedImageUri);
        if (savedImagePath == null) {
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert room with description
        boolean inserted = MyDB.insertRoom(name, savedImagePath, category, description, price, status);

        if (inserted) {
            Toast.makeText(this, "Room added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error adding room", Toast.LENGTH_SHORT).show();
        }
    }

    private String copyImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) return null;

            File dir = new File(getFilesDir(), "room_images");
            if (!dir.exists()) dir.mkdirs();

            String filename = "room_" + System.currentTimeMillis() + ".jpg";
            File outFile = new File(dir, filename);

            FileOutputStream outputStream = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return outFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
