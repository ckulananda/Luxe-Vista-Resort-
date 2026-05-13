package com.example.db_luxevista_resort;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminManageRoomsActivity extends AppCompatActivity {

    private LuxeDB db;
    private RecyclerView recyclerView;
    private AdminRoomAdapter adapter;
    private List<RoomModel> roomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_rooms);

        db = new LuxeDB(this);
        recyclerView = findViewById(R.id.recyclerAdminRooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadRooms();
    }

    private void loadRooms() {
        roomList = new ArrayList<>();
        Cursor cursor = db.getAllRooms();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("roomID"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("roomName"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("roomImagePath")); // Fetch image path
                String category = cursor.getString(cursor.getColumnIndexOrThrow("roomCategory"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("roomDescription"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("roomPrice"));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow("roomStatus"));

                roomList.add(new RoomModel(id, name, imagePath, category, description, price, status));
            }
            cursor.close();
        }

        // Attach adapter to RecyclerView
        adapter = new AdminRoomAdapter(this, roomList, new AdminRoomAdapter.OnRoomActionListener() {
            @Override
            public void onUpdateRoom(RoomModel room) {
                Intent intent = new Intent(AdminManageRoomsActivity.this, UpdateRoomActivity.class);
                intent.putExtra("roomID", room.getId());
                intent.putExtra("roomName", room.getName());
                intent.putExtra("roomImagePath", room.getImagePath());
                intent.putExtra("roomCategory", room.getCategory());
                intent.putExtra("roomDescription", room.getDescription());
                intent.putExtra("roomPrice", room.getPrice());
                intent.putExtra("roomStatus", room.getStatus());
                startActivity(intent);
            }

            @Override
            public void onDeleteRoom(RoomModel room) {
                // Check if room has existing bookings
                int bookingCount = db.getBookingCountForRoom(room.getId());

                if (bookingCount > 0) {
                    // Show warning to admin
                    Toast.makeText(AdminManageRoomsActivity.this,
                            "Cannot delete room! There are " + bookingCount + " existing bookings.",
                            Toast.LENGTH_LONG).show();
                } else {
                    // Safe to delete
                    boolean deleted = db.deleteRoom(room.getId());
                    if (deleted) {
                        Toast.makeText(AdminManageRoomsActivity.this,
                                "Room deleted successfully", Toast.LENGTH_SHORT).show();
                        loadRooms(); // Refresh list
                    } else {
                        Toast.makeText(AdminManageRoomsActivity.this,
                                "Failed to delete room", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRooms(); // Refresh list after update or delete
    }
}
