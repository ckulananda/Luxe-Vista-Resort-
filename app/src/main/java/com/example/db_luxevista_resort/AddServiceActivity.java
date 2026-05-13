package com.example.db_luxevista_resort;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddServiceActivity extends AppCompatActivity {

    private EditText etName, etPrice, etDuration, etDescription, etStartTime, etEndTime;
    private Button btnAdd;
    private LuxeDB db;

    private int startHour, startMinute, endHour, endMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        db = new LuxeDB(this);

        etName = findViewById(R.id.etServiceName);
        etPrice = findViewById(R.id.etServicePrice);
        etDuration = findViewById(R.id.etServiceDuration);
        etDescription = findViewById(R.id.etServiceDescription);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        btnAdd = findViewById(R.id.btnAddService);

        // Open TimePicker when Start Time is clicked
        etStartTime.setOnClickListener(v -> showTimePicker(true));

        // Open TimePicker when End Time is clicked
        etEndTime.setOnClickListener(v -> showTimePicker(false));

        // Add service button click
        btnAdd.setOnClickListener(v -> addService());
    }

    // Show TimePickerDialog
    private void showTimePicker(boolean isStartTime) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String timeText = String.format("%02d:%02d", hourOfDay, minute1);

            if (isStartTime) {
                startHour = hourOfDay;
                startMinute = minute1;
                etStartTime.setText(timeText);
            } else {
                endHour = hourOfDay;
                endMinute = minute1;
                etEndTime.setText(timeText);
            }

            // If both times are set, calculate duration
            if (!etStartTime.getText().toString().isEmpty() && !etEndTime.getText().toString().isEmpty()) {
                calculateDuration();
            }

        }, hour, minute, true);

        timePicker.show();
    }

    // Calculate duration in minutes
    private void calculateDuration() {
        int startTotal = startHour * 60 + startMinute;
        int endTotal = endHour * 60 + endMinute;
        int duration = endTotal - startTotal;

        if (duration < 0) {
            // Handle overnight or wrong input
            Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show();
            etDuration.setText("");
        } else {
            etDuration.setText(String.valueOf(duration));
        }
    }

    // Add service to database
    private void addService() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || durationStr.isEmpty() || description.isEmpty()
                || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int duration = Integer.parseInt(durationStr);

        boolean success = db.insertService(name, price, duration, description, startTime, endTime);
        if (success) {
            Toast.makeText(this, "Service added successfully", Toast.LENGTH_SHORT).show();
            finish(); // close activity
        } else {
            Toast.makeText(this, "Failed to add service", Toast.LENGTH_SHORT).show();
        }
    }
}
