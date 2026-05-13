package com.example.db_luxevista_resort;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddBookingActivity extends AppCompatActivity {

    private TextView tvUserInfo;
    private RecyclerView recyclerRooms;

    private LuxeDB db;
    private List<RoomModel> roomList;
    private RoomAdapter adapter;

    private int userID;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);

        // UI
        tvUserInfo = findViewById(R.id.tvUserInfo);
        recyclerRooms = findViewById(R.id.recyclerRooms);
        recyclerRooms.setLayoutManager(new LinearLayoutManager(this));

        // Database
        db = new LuxeDB(this);

        // Get current logged-in user from GlobalVar
        userID = GlobalVar.userID;
        userName = GlobalVar.fName + " " + GlobalVar.lName;

        tvUserInfo.setText("User: " + userName + " (ID: " + userID + ")");

        // Load all rooms
        roomList = db.getAllRoomList(); // ensure this method exists in LuxeDB
        if (roomList == null) roomList = new ArrayList<>();

        // Adapter with click listener for booking
        adapter = new RoomAdapter(this, roomList, room -> {
            if (room.getStatus() == 1) { // Only allow if available
                // Show confirmation dialog
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Confirm Booking")
                        .setMessage("Do you want to book the room: " + room.getName() + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            bookRoom(room.getId(), userID, room.getPrice());
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                Toast.makeText(this, "This room is unavailable!", Toast.LENGTH_SHORT).show();
            }
        });


        recyclerRooms.setAdapter(adapter);
    }

    private void bookRoom(int roomID, int customerID, double roomPrice) {
        String category = "Standard"; // or room.getCategory() if you want dynamic
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        boolean success = db.insertBooking(roomID, customerID, category, roomPrice, date);

        if (success) {
            Toast.makeText(this, "Booking successful!", Toast.LENGTH_SHORT).show();
            // Generate PDF receipt
            generateBookingPDF(roomID, customerID, userName, category, roomPrice, date);
        } else {
            Toast.makeText(this, "Booking failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateBookingPDF(int roomID, int customerID, String userName,
                                    String category, double price, String date) {
        try {
            // File path in Downloads folder
            String fileName = "Booking_" + System.currentTimeMillis() + ".pdf";
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, fileName);

            PdfDocument pdfDocument = new PdfDocument();
            Paint paint = new Paint();

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            // 1️⃣ Draw Logo at Top (center)
            Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo); // replace with your logo
            Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, 120, 120, false);
            int centerX = (canvas.getWidth() - scaledLogo.getWidth()) / 2;
            canvas.drawBitmap(scaledLogo, centerX, 40, paint);

            // 2️⃣ Header Title
            paint.setColor(Color.BLACK);
            paint.setTextSize(22f);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
            canvas.drawText("LuxeVista Resort - Booking Receipt", 110, 200, paint);

            // Divider
            paint.setStrokeWidth(2);
            canvas.drawLine(60, 220, canvas.getWidth() - 60, 220, paint);

            // 3️⃣ Booking Details
            paint.setTextSize(16f);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            int y = 260;
            canvas.drawText("Booking ID: " + System.currentTimeMillis(), 80, y, paint); y += 30;
            canvas.drawText("User: " + userName + " (ID: " + customerID + ")", 80, y, paint); y += 30;
            canvas.drawText("Room ID: " + roomID, 80, y, paint); y += 30;
            canvas.drawText("Category: " + category, 80, y, paint); y += 30;
            canvas.drawText("Price: $" + price, 80, y, paint); y += 30;
            canvas.drawText("Date: " + date, 80, y, paint); y += 50;

            // 4️⃣ Thank You Note
            paint.setTextSize(16f);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
            canvas.drawText("Thank you for booking with LuxeVista Resort!", 80, y, paint); y += 30;
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            canvas.drawText("We will contact you shortly via email and phone.", 80, y, paint);

            // 5️⃣ Footer
            paint.setTextSize(12f);
            paint.setColor(Color.DKGRAY);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Generated on: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()),
                    canvas.getWidth() / 2, 800, paint);
            canvas.drawText("© LuxeVista Resort", canvas.getWidth() / 2, 820, paint);

            pdfDocument.finishPage(page);

            // Save file
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();

            Toast.makeText(this, "PDF saved in Downloads: " + fileName, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
