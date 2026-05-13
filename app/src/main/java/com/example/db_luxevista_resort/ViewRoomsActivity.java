package com.example.db_luxevista_resort;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewRoomsActivity extends AppCompatActivity {

    private LuxeDB db;
    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private List<RoomModel> roomList;

    private TextView pakages,OurResort,SingIn,AboutUs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rooms);

        recyclerView = findViewById(R.id.recyclerRooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = new LuxeDB(this);
        roomList = new ArrayList<>();

        loadRooms();
        // Navigation buttons
        pakages = findViewById(R.id.navPackages);
        OurResort = findViewById(R.id.navResort);
        SingIn = findViewById(R.id.navSignIn);
        AboutUs = findViewById(R.id.navAbout);

        pakages.setOnClickListener(v ->
                startActivity(new Intent(ViewRoomsActivity.this, PackagesActivity.class))
        );

        OurResort.setOnClickListener(v ->
                startActivity(new Intent(ViewRoomsActivity.this, ResortActivity.class))
        );

        SingIn.setOnClickListener(v ->
                startActivity(new Intent(ViewRoomsActivity.this, MainActivity.class))
        );

        AboutUs.setOnClickListener(v ->
                startActivity(new Intent(ViewRoomsActivity.this, AboutUsActivity.class))
        );
    }

    private void loadRooms() {
        roomList.clear();

        Cursor cursor = null;
        try {
            cursor = db.getAllRooms();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(LuxeDB.ROOM_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(LuxeDB.ROOM_NAME));
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(LuxeDB.ROOM_IMAGE_PATH));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow(LuxeDB.ROOM_CATEGORY));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(LuxeDB.ROOM_DESCRIPTION)); // new
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(LuxeDB.ROOM_PRICE));
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(LuxeDB.ROOM_STATUS));

                    roomList.add(new RoomModel(id, name, imagePath, category, description, price, status));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        // Use adapter without lambda; image click handled inside RoomAdapter
        if (adapter == null) {
            adapter = new RoomAdapter(this, roomList, room -> {
                // Handle room click
                Toast.makeText(this, "Please log in to continue: " + room.getName(), Toast.LENGTH_SHORT).show();
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
