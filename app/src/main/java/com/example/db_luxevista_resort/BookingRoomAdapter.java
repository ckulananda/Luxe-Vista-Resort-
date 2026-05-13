package com.example.db_luxevista_resort;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingRoomAdapter extends RecyclerView.Adapter<BookingRoomAdapter.BookingViewHolder> {

    private Context context;
    private List<BookingDetailModel> bookingList;
    private OnBookingActionListener listener;

    public interface OnBookingActionListener {
        void onDeleteBooking(BookingDetailModel booking);
        void onViewBooking(BookingDetailModel booking);
    }

    public BookingRoomAdapter(Context context, List<BookingDetailModel> bookingList, OnBookingActionListener listener) {
        this.context = context;
        this.bookingList = bookingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingDetailModel booking = bookingList.get(position);

        holder.tvBookingId.setText("Booking ID: " + booking.getBookingID());
        holder.tvRoomId.setText("Room Name: " + booking.getRoomName());
        holder.tvCategory.setText("Category: " + booking.getBookingCategory());
        holder.tvPrice.setText("Price: Rs." + booking.getPrice());
        holder.tvDate.setText("Date: " + booking.getBookingDate());
        holder.tvUserName.setText("User: " + booking.getUserName());

        // Long click to delete
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDeleteBooking(booking);
            }
            return true;
        });

        // Click to view details
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewBooking(booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingId, tvRoomId, tvCategory, tvPrice, tvDate, tvUserName;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvRoomId = itemView.findViewById(R.id.tvRoomId);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvUserName = itemView.findViewById(R.id.tvUserName);
        }
    }
}
