package com.example.db_luxevista_resort;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyReservationActivity extends AppCompatActivity {

    RecyclerView recycler;
    LuxeDB db;
    ArrayList<ReservationModel> reservations;
    boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservation);

        recycler = findViewById(R.id.reservationRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        db = new LuxeDB(this);

        isAdmin = GlobalVar.nameU.equals("Admin");
        loadData();
    }

    private void loadData() {
        reservations = new ArrayList<>();
        Cursor c = isAdmin ?
                db.getAllReservations() :
                db.getReservationsForUser(GlobalVar.userID);

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndexOrThrow(LuxeDB.RESERVATION_ID));
                int uid = c.getInt(c.getColumnIndexOrThrow(LuxeDB.RESERVATION_CUSTOMER_ID));
                String type = c.getString(c.getColumnIndexOrThrow(LuxeDB.RESERVATION_TYPE));
                String date = c.getString(c.getColumnIndexOrThrow(LuxeDB.RESERVATION_DATE));
                String status = c.getString(c.getColumnIndexOrThrow(LuxeDB.RESERVATION_STATUS));
                reservations.add(new ReservationModel(id, uid, type, date, status));
            } while (c.moveToNext());
        }
        c.close();

        recycler.setAdapter(new ReservationAdapter(this, reservations, isAdmin));
    }
}
