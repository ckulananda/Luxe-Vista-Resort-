package com.example.db_luxevista_resort;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingRoomAdapter adapter;
    private List<BookingDetailModel> bookingList; // Changed to BookingDetailModel
    private LuxeDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookings);

        recyclerView = findViewById(R.id.recyclerBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = new LuxeDB(this);
        bookingList = new ArrayList<>();

        loadBookings();
    }

    private void loadBookings() {
        bookingList.clear();

        // Get the current user's ID from your GlobalVar
        int currentUserId = GlobalVar.userID;

        if (currentUserId == -1) {
            Toast.makeText(this, "Please log in to view your bookings.", Toast.LENGTH_LONG).show();
            return;
        }

        Cursor cursor = null;
        try {
            cursor = db.getBookingsForUser(currentUserId);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int bookingID = cursor.getInt(cursor.getColumnIndexOrThrow(LuxeDB.BOOKING_ID));
                    String roomName = cursor.getString(cursor.getColumnIndexOrThrow(LuxeDB.ROOM_NAME));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(LuxeDB.ROOM_PRICE));
                    String userName = cursor.getString(cursor.getColumnIndexOrThrow(LuxeDB.USER_USERNAME));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow(LuxeDB.BOOKING_CATEGORY));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(LuxeDB.BOOKING_DATE));

                    bookingList.add(new BookingDetailModel(bookingID, roomName, price, userName, category, date));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading bookings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        adapter = new BookingRoomAdapter(this, bookingList, new BookingRoomAdapter.OnBookingActionListener() {
            @Override
            public void onDeleteBooking(BookingDetailModel booking) {
                showDeleteConfirmationDialog(booking);
            }

            @Override
            public void onViewBooking(BookingDetailModel booking) {
                Toast.makeText(ViewBookingsActivity.this, "Viewing details for Booking ID: " + booking.getBookingID(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);

        if (bookingList.isEmpty()) {
            Toast.makeText(this, "No bookings found for your account.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog(BookingDetailModel booking) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Booking")
                .setMessage("Are you sure you want to delete this booking?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Pass the booking ID to the delete method
                    if (db.deleteBooking(booking.getBookingID())) {
                        Toast.makeText(ViewBookingsActivity.this, "Booking deleted successfully.", Toast.LENGTH_SHORT).show();
                        loadBookings(); // Reload data
                    } else {
                        Toast.makeText(ViewBookingsActivity.this, "Failed to delete booking.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}