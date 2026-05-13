package com.example.db_luxevista_resort;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private LuxeDB db;
    private UserAdapter adapter;
    private EditText etSearch;
    private RecyclerView rv;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        db = new LuxeDB(this);
        etSearch = findViewById(R.id.etSearchUser);
        rv = findViewById(R.id.rvUsers);
        tvEmpty = findViewById(R.id.tvEmpty); // Add this in your layout
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdapter(new ArrayList<>(), new UserAdapter.OnUserAction() {
            @Override
            public void onEdit(UserModel user) {
                showEditDialog(user);
            }

            @Override
            public void onDelete(UserModel user) {
                deleteUser(user);
            }
        });
        rv.setAdapter(adapter);

        // Load all users initially
        loadUsers("");

        // Search action
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            loadUsers(etSearch.getText().toString().trim());
            return true;
        });

        // Add new user button
        findViewById(R.id.btnAddUser).setOnClickListener(v -> showAddDialog());
    }

    // ---------------- LOAD USERS ----------------
    private void loadUsers(String keyword) {
        List<UserModel> list = new ArrayList<>();
        Cursor c;

        if (keyword.isEmpty()) {
            c = db.getAllUsers();   // Implement this in LuxeDB
        } else {
            c = db.searchUser(keyword); // Implement searchUser() in LuxeDB
        }

        if (c != null && c.moveToFirst()) {
            do {
                list.add(new UserModel(
                        c.getInt(c.getColumnIndexOrThrow(LuxeDB.USER_ID)),
                        c.getString(c.getColumnIndexOrThrow(LuxeDB.USER_USERNAME)),
                        c.getString(c.getColumnIndexOrThrow(LuxeDB.USER_EMAIL)),
                        c.getString(c.getColumnIndexOrThrow(LuxeDB.USER_PROFILE_PATH)),
                        c.getString(c.getColumnIndexOrThrow(LuxeDB.USER_FNAME)),
                        c.getString(c.getColumnIndexOrThrow(LuxeDB.USER_LNAME)),
                        c.getString(c.getColumnIndexOrThrow(LuxeDB.USER_ADDRESS)),
                        c.getString(c.getColumnIndexOrThrow(LuxeDB.USER_CONTACT))
                ));
            } while (c.moveToNext());
            c.close();
        }

        adapter.updateData(list);

        // Show empty message if no users
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // ---------------- DELETE ----------------
    private void deleteUser(UserModel user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Delete " + user.username + "?")
                .setPositiveButton("Delete", (d, w) -> {
                    if (db.deleteUser(user.id)) {
                        Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                        loadUsers(etSearch.getText().toString().trim());
                    } else {
                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- EDIT ----------------
    private void showEditDialog(UserModel user) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_user, null);
        EditText etF = dialogView.findViewById(R.id.etFName);
        EditText etL = dialogView.findViewById(R.id.etLName);
        EditText etAddr = dialogView.findViewById(R.id.etAddress);
        EditText etContact = dialogView.findViewById(R.id.etContact);

        etF.setText(user.fname);
        etL.setText(user.lname);
        etAddr.setText(user.address);
        etContact.setText(user.contact);

        new AlertDialog.Builder(this)
                .setTitle("Edit " + user.username)
                .setView(dialogView)
                .setPositiveButton("Save", (d, w) -> {
                    boolean ok = db.updateUser(user.id,
                            etF.getText().toString(),
                            etL.getText().toString(),
                            etAddr.getText().toString(),
                            etContact.getText().toString(),
                            user.profilePath);
                    if (ok) {
                        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                        loadUsers(etSearch.getText().toString().trim());
                    } else {
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- ADD ----------------
    private void showAddDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_user, null);
        EditText etUsername = dialogView.findViewById(R.id.etUsername);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);
        EditText etF = dialogView.findViewById(R.id.etFName);
        EditText etL = dialogView.findViewById(R.id.etLName);
        EditText etAddr = dialogView.findViewById(R.id.etAddress);
        EditText etContact = dialogView.findViewById(R.id.etContact);

        new AlertDialog.Builder(this)
                .setTitle("Add New User")
                .setView(dialogView)
                .setPositiveButton("Add", (d, w) -> {
                    boolean ok = db.insertUser(
                            etUsername.getText().toString(),
                            etEmail.getText().toString(),
                            etPassword.getText().toString(),
                            "", // profilePath
                            etF.getText().toString(),
                            etL.getText().toString(),
                            etAddr.getText().toString(),
                            etContact.getText().toString()
                    );
                    if (ok) {
                        Toast.makeText(this, "User added", Toast.LENGTH_SHORT).show();
                        loadUsers("");
                    } else {
                        Toast.makeText(this, "Insert failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
