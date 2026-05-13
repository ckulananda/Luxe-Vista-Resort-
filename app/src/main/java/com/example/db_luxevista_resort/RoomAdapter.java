package com.example.db_luxevista_resort;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private final Context context;
    private final List<RoomModel> roomList;
    private final OnRoomClickListener listener; //

    public interface OnRoomClickListener {
        void onRoomClick(RoomModel room);
    }


    public RoomAdapter(Context context, List<RoomModel> roomList, OnRoomClickListener listener) {
        this.context = context;
        this.roomList = roomList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomModel room = roomList.get(position);

        // Set text info
        holder.txtRoomName.setText(room.getName());
        holder.txtRoomCategory.setText("Category: " + room.getCategory());
        holder.txtRoomPrice.setText("Price: Rs." + room.getPrice());
        holder.txtRoomStatus.setText(room.getStatus() == 1 ? "Available" : "Unavailable"); // 0 = Available

        // Load image from stored path
        loadRoomImage(holder.imgRoom, room.getImagePath());

        // Click listener on item for booking
        holder.itemView.setOnClickListener(v -> {
            if (room.getStatus() == 1 && listener != null) {
                listener.onRoomClick(room);
            } else {
                Toast.makeText(context, "This room is unavailable!", Toast.LENGTH_SHORT).show();
            }
        });

        // Click listener on image (optional)
        holder.imgRoom.setOnClickListener(v ->
                Toast.makeText(context, "Room image clicked", Toast.LENGTH_SHORT).show()
        );
    }







    @Override
    public int getItemCount() {
        return roomList.size();
    }


    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRoom;
        TextView txtRoomName, txtRoomCategory, txtRoomPrice, txtRoomStatus;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            txtRoomName = itemView.findViewById(R.id.txtRoomName);
            txtRoomCategory = itemView.findViewById(R.id.txtRoomCategory);
            txtRoomPrice = itemView.findViewById(R.id.txtRoomPrice);
            txtRoomStatus = itemView.findViewById(R.id.txtRoomAvailability); // Make sure ID matches XML
        }
    }




    private void loadRoomImage(ImageView imageView, String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            imageView.setImageResource(R.mipmap.ic_launcher_round);
            return;
        }

        try {
            // Load directly from file path
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.mipmap.ic_launcher_round);
            }
        } catch (Exception e) {
            e.printStackTrace();
            imageView.setImageResource(R.mipmap.ic_launcher_round);
        }
    }




}
