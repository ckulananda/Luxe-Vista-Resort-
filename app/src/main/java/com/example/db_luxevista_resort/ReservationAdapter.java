package com.example.db_luxevista_resort;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ResViewHolder> {

    private Context ctx;
    private ArrayList<ReservationModel> list;
    private boolean isAdmin;
    private LuxeDB db;

    public ReservationAdapter(Context ctx, ArrayList<ReservationModel> list, boolean isAdmin) {
        this.ctx = ctx;
        this.list = list;
        this.isAdmin = isAdmin;
        db = new LuxeDB(ctx);
    }

    @NonNull
    @Override
    public ResViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_reservation, parent, false);
        return new ResViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ResViewHolder h, int pos) {
        ReservationModel m = list.get(pos);
        h.tvResId.setText("Reservation ID: " + m.id);
        h.tvResType.setText("Type: " + m.type);
        h.tvResDate.setText("Date: " + m.date);
        h.tvResStatus.setText("Status: " + m.status);

        if (isAdmin) {
            h.spStatus.setVisibility(View.VISIBLE);
            ArrayAdapter<String> spAdapter = new ArrayAdapter<>(
                    ctx,
                    android.R.layout.simple_spinner_dropdown_item,
                    new String[]{"Pending","Confirmed","Cancelled"}
            );
            h.spStatus.setAdapter(spAdapter);
            h.spStatus.setSelection(spAdapter.getPosition(m.status));

            h.spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    String newStatus = parent.getItemAtPosition(pos).toString();
                    if (!newStatus.equals(m.status)) {
                        if (db.updateReservationStatus(m.id, newStatus)) {
                            m.status = newStatus;
                            Toast.makeText(ctx,"Updated to "+newStatus, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ResViewHolder extends RecyclerView.ViewHolder {
        TextView tvResId, tvResType, tvResDate, tvResStatus;
        Spinner spStatus;
        ResViewHolder(View v){
            super(v);
            tvResId = v.findViewById(R.id.tvResId);
            tvResType = v.findViewById(R.id.tvResType);
            tvResDate = v.findViewById(R.id.tvResDate);
            tvResStatus = v.findViewById(R.id.tvResStatus);
            spStatus = v.findViewById(R.id.spStatus);
        }
    }
}
