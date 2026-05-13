package com.example.db_luxevista_resort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserVH> {

    private List<UserModel> userList = new ArrayList<>();
    private final OnUserAction listener;

    public interface OnUserAction {
        void onEdit(UserModel user);
        void onDelete(UserModel user);
    }

    public UserAdapter(List<UserModel> list, OnUserAction listener) {
        if (list != null) this.userList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_card, parent, false);
        return new UserVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVH holder, int position) {
        UserModel u = userList.get(position);

        // Bind username + email safely
        holder.username.setText(u.username != null ? u.username : "No Username");
        holder.email.setText(u.email != null ? u.email : "No Email");

        // Click listeners
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(u);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(u);
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    // Call this when updating list after DB changes
    public void updateData(List<UserModel> newList) {
        this.userList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class UserVH extends RecyclerView.ViewHolder {
        TextView username, email;
        MaterialButton btnEdit, btnDelete;

        UserVH(View v) {
            super(v);
            username = v.findViewById(R.id.tvUsername);
            email = v.findViewById(R.id.tvEmail);
            btnEdit = v.findViewById(R.id.btnEdit);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
