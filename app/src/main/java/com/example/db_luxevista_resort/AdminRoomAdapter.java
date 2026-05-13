package com.example.db_luxevista_resort;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class AdminRoomAdapter extends RecyclerView.Adapter<AdminRoomAdapter.AdminRoomViewHolder> {

    public interface OnRoomActionListener {
        void onUpdateRoom(RoomModel room);
        void onDeleteRoom(RoomModel room);
    }

    private final Context context;
    private final List<RoomModel> roomList;
    private final OnRoomActionListener listener;

    public AdminRoomAdapter(Context context, List<RoomModel> roomList, OnRoomActionListener listener) {
        this.context = context;
        this.roomList = roomList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_admin, parent, false);
        return new AdminRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminRoomViewHolder holder, int position) {
        RoomModel room = roomList.get(position);

        holder.txtRoomName.setText(room.getName());
        holder.txtRoomCategory.setText("Category: " + room.getCategory());
        holder.txtRoomPrice.setText("Price: Rs. " + room.getPrice());
        holder.txtRoomStatus.setText(room.getStatus() == 1 ? "Available" : "Unavailable");

        // Load image safely
        loadRoomImage(holder.imgRoom, room.getImagePath());

        // Update button
        holder.btnUpdate.setOnClickListener(v -> {
            if (listener != null) listener.onUpdateRoom(room);
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteRoom(room);
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class AdminRoomViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRoom;
        TextView txtRoomName, txtRoomCategory, txtRoomPrice, txtRoomStatus;
        Button btnUpdate, btnDelete;

        public AdminRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            txtRoomName = itemView.findViewById(R.id.txtRoomName);
            txtRoomCategory = itemView.findViewById(R.id.txtRoomCategory);
            txtRoomPrice = itemView.findViewById(R.id.txtRoomPrice);
            txtRoomStatus = itemView.findViewById(R.id.txtRoomStatus);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private void loadRoomImage(ImageView imageView, String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            imageView.setImageResource(R.mipmap.ic_launcher_round);
            return;
        }

        try {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.mipmap.ic_launcher_round);
                }
            } else {
                imageView.setImageResource(R.mipmap.ic_launcher_round);
            }
        } catch (Exception e) {
            e.printStackTrace();
            imageView.setImageResource(R.mipmap.ic_launcher_round);
        }
    }
}
