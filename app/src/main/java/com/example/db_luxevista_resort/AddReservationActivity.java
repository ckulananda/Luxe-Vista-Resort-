package com.example.db_luxevista_resort;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddReservationActivity extends AppCompatActivity {

    private TextView tvUserInfo, tvSelectedDate, tvServiceDetails;
    private Spinner spinnerType, spinnerServiceName;
    private Button btnSelectDate, btnAddReservation;

    private LuxeDB db;
    private int userID;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);

        tvUserInfo = findViewById(R.id.tvUserInfo);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvServiceDetails = findViewById(R.id.tvServiceDetails);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerServiceName = findViewById(R.id.spinnerServiceName);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnAddReservation = findViewById(R.id.btnAddReservation);

        db = new LuxeDB(this);

        userID = getIntent().getIntExtra("userID", -1);
        userName = getIntent().getStringExtra("userName");

        tvUserInfo.setText("User: " + userName + " (ID: " + userID + ")");

        // Type Spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Service"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        loadServiceNames();

        spinnerServiceName.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                showServiceDetails(spinnerServiceName.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnSelectDate.setOnClickListener(v -> showDatePicker());

        btnAddReservation.setOnClickListener(v -> addReservation());
    }

    private void loadServiceNames() {
        List<String> services = new ArrayList<>();
        Cursor cursor = db.getAllServices();
        if (cursor.moveToFirst()) {
            do {
                services.add(cursor.getString(cursor.getColumnIndexOrThrow(LuxeDB.SERVICE_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                services);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServiceName.setAdapter(adapter);
    }

    private void showServiceDetails(String serviceName) {
        Cursor cursor = db.getAllServices();
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndexOrThrow(LuxeDB.SERVICE_NAME)).equals(serviceName)) {
                    String details = "Price: Rs." + cursor.getDouble(cursor.getColumnIndexOrThrow(LuxeDB.SERVICE_PRICE)) +
                            "\nDuration: " + cursor.getInt(cursor.getColumnIndexOrThrow(LuxeDB.SERVICE_DURATION)) + " mins" +
                            "\nStart Time: " + cursor.getString(cursor.getColumnIndexOrThrow(LuxeDB.SERVICE_DURATION_START_TIME)) +
                            "\nEnd Time: " + cursor.getString(cursor.getColumnIndexOrThrow(LuxeDB.SERVICE_DURATION_END_TIME)) +
                            "\nDescription: " + cursor.getString(cursor.getColumnIndexOrThrow(LuxeDB.SERVICE_DESCRIPTION));
                    tvServiceDetails.setText(details);
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    tvSelectedDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    private void addReservation() {
        String type = spinnerType.getSelectedItem().toString();
        String serviceName = spinnerServiceName.getSelectedItem().toString();
        String date = tvSelectedDate.getText().toString();

        if (date.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        long resID = db.insertReservation(userID, date, "Pending", type + ": " + serviceName);

        if (resID != -1) {
            Toast.makeText(this, "Reservation added!", Toast.LENGTH_SHORT).show();
            generateReservationPDF(resID, userID, userName, type, serviceName, date);
            finish();
        } else {
            Toast.makeText(this, "Failed to add reservation.", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateReservationPDF(long reservationID, int userID, String userName,
                                        String type, String serviceName, String date) {

        PdfDocument pdf = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        int y = 50;

        // Draw Logo
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        canvas.drawBitmap(Bitmap.createScaledBitmap(logo, 120, 120, false),
                pageInfo.getPageWidth() / 2 - 60, y, paint);
        y += 140;

        // Draw Title
        paint.setTextSize(22);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
        canvas.drawText("LuxeVista Resort Reservation", pageInfo.getPageWidth() / 2, y, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setFakeBoldText(false);
        y += 40;

        // Fetch service details from DB
        LuxeDB db = new LuxeDB(this);
        Cursor cursor = db.getAllServices(); // or a method to get service by name
        double servicePrice = 0;
        int serviceDuration = 0;
        String serviceDescription = "";
        if (cursor.moveToFirst()) {
            do {
                String sName = cursor.getString(cursor.getColumnIndexOrThrow(LuxeDB.SERVICE_NAME));
                if (sName.equals(serviceName)) {
                    servicePrice = cursor.getDouble(cursor.getColumnIndexOrThrow(LuxeDB.SERVICE_PRICE));
                    serviceDuration = cursor.getInt(cursor.getColumnIndexOrThrow(LuxeDB.SERVICE_DURATION));
                    serviceDescription = cursor.getString(cursor.getColumnIndexOrThrow(LuxeDB.SERVICE_DESCRIPTION));
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Draw table rectangle for reservation details
        int left = 50, right = pageInfo.getPageWidth() - 50;
        int rowHeight = 30;
        int currentY = y;

        String[][] data = {
                {"User", userName + " (ID: " + userID + ")"},
                {"Reservation ID", String.valueOf(reservationID)},
                {"Type", type},
                {"Service Name", serviceName},
                {"Date", date},
                {"Status", "Pending"}
        };

        paint.setTextSize(16);
        for (String[] row : data) {
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(left, currentY, right, currentY + rowHeight, paint);
            paint.setFakeBoldText(true);
            canvas.drawText(row[0], left + 10, currentY + 22, paint);
            paint.setFakeBoldText(false);
            canvas.drawText(row[1], left + 200, currentY + 22, paint);
            currentY += rowHeight;
        }

        y = currentY + 20;

        // Service Details Table
        canvas.drawText("Service Details", left, y, paint);
        y += 10;

        String[][] serviceData = {
                {"Price", "Rs." + servicePrice},
                {"Duration", serviceDuration + " mins"},
                {"Description", serviceDescription}
        };

        for (String[] row : serviceData) {
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(left, y, right, y + rowHeight, paint);
            paint.setFakeBoldText(true);
            canvas.drawText(row[0], left + 10, y + 22, paint);
            paint.setFakeBoldText(false);
            canvas.drawText(row[1], left + 200, y + 22, paint);
            y += rowHeight;
        }

        y += 40;

        // Footer
        paint.setTextSize(14);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Thank you for choosing LuxeVista Resort!", pageInfo.getPageWidth() / 2, y, paint);
        y += 20;
        canvas.drawText("Hotel will contact you via mobile or email.", pageInfo.getPageWidth() / 2, y, paint);

        pdf.finishPage(page);

        // Save PDF to Downloads folder
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, "Reservation_" + reservationID + ".pdf");

        try {
            pdf.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            pdf.close();
        }
    }

}
