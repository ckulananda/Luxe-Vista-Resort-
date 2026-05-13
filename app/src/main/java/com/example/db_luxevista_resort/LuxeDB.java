package com.example.db_luxevista_resort;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class LuxeDB extends SQLiteOpenHelper {

    private static final String TAG = "LuxeDB";

    private ArrayList<UserModel> userList;
    private UserAdapter adapter;
    private LuxeDB dbHelper;

    // Database info
    private static final String DATABASE_NAME = "luxeVistaDB.db";
    private static final int DATABASE_VERSION = 1; // increment version

    // Tables
    public static final String TABLE_USERS = "users";
    public static final String TABLE_ROOMS = "tblLuxeRooms";
    public static final String TABLE_BOOKINGS = "tblLuxeRoomBooking";
    public static final String TABLE_SERVICES = "tblService";
    public static final String TABLE_RESERVATIONS = "tblReservation";

    // Users Table
    public static final String USER_ID = "id";
    public static final String USER_USERNAME = "username";
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password";
    public static final String USER_FNAME = "firstName";
    public static final String USER_LNAME = "lastName";
    public static final String USER_CONTACT = "contact";
    public static final String USER_ADDRESS = "address";
    public static final String USER_PROFILE_PATH = "profilePicPath";

    // Rooms Table
    public static final String ROOM_ID = "roomID";
    public static final String ROOM_NAME = "roomName";
    public static final String ROOM_IMAGE_PATH = "roomImagePath";
    public static final String ROOM_CATEGORY = "roomCategory";
    public static final String ROOM_DESCRIPTION = "roomDescription";
    public static final String ROOM_PRICE = "roomPrice";
    public static final String ROOM_STATUS = "roomStatus";

    // Booking Table
    public static final String BOOKING_ID = "bookingID";
    public static final String BOOKING_ROOM_ID = "roomID";
    public static final String BOOKING_CUSTOMER_ID = "customerID";
    public static final String BOOKING_CATEGORY = "bookingCategory";
    public static final String BOOKING_PRICE = "bookingPrice";
    public static final String BOOKING_DATE = "bookingDate";

    // Service Table
    public static final String SERVICE_ID = "serviceID";
    public static final String SERVICE_NAME = "serviceName";
    public static final String SERVICE_PRICE = "servicePrice";
    public static final String SERVICE_DURATION = "duration";
    public static final String SERVICE_DESCRIPTION = "description";
    public static final String SERVICE_DURATION_START_TIME = "start_time";
    public static final String SERVICE_DURATION_END_TIME   = "end_time";





    // Reservation Table
    public static final String RESERVATION_ID = "reservationID";
    public static final String RESERVATION_CUSTOMER_ID = "customerID";
    public static final String RESERVATION_DATE = "date";
    public static final String RESERVATION_RESPOND = "respond";
    public static final String RESERVATION_TYPE = "type";
    public static final String RESERVATION_STATUS = "status";

    private final Context context;

    public LuxeDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        createImageFolder();
    }

    // -------------------- IMAGE FOLDER --------------------
    private void createImageFolder() {
        File folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "LuxeVistaImages");
        if (!folder.exists() && folder.mkdirs()) {
            Log.d(TAG, "Image folder created: " + folder.getAbsolutePath());
        }
    }

    // -------------------- CONFIG --------------------
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // -------------------- DATABASE --------------------
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_USERNAME + " TEXT UNIQUE, " +
                USER_EMAIL + " TEXT UNIQUE, " +
                USER_PASSWORD + " TEXT, " +
                USER_PROFILE_PATH + " TEXT, " +
                USER_FNAME + " TEXT, " +
                USER_LNAME + " TEXT, " +
                USER_ADDRESS + " TEXT, " +
                USER_CONTACT + " TEXT)";
        db.execSQL(createUsers);

        String createRooms = "CREATE TABLE " + TABLE_ROOMS + " (" +
                ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ROOM_NAME + " TEXT, " +
                ROOM_IMAGE_PATH + " TEXT, " +
                ROOM_CATEGORY + " TEXT, " +
                ROOM_DESCRIPTION + " TEXT, " +     // <-- add this
                ROOM_PRICE + " REAL, " +
                ROOM_STATUS + " INTEGER DEFAULT 0)";
        db.execSQL(createRooms);


        String createBookings = "CREATE TABLE " + TABLE_BOOKINGS + " (" +
                BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BOOKING_ROOM_ID + " INTEGER, " +
                BOOKING_CUSTOMER_ID + " INTEGER, " +
                BOOKING_CATEGORY + " TEXT, " +
                BOOKING_PRICE + " REAL, " +
                BOOKING_DATE + " TEXT, " +
                "FOREIGN KEY(" + BOOKING_ROOM_ID + ") REFERENCES " + TABLE_ROOMS + "(" + ROOM_ID + "), " +
                "FOREIGN KEY(" + BOOKING_CUSTOMER_ID + ") REFERENCES " + TABLE_USERS + "(" + USER_ID + "))";
        db.execSQL(createBookings);

        String createServices =
                "CREATE TABLE " + TABLE_SERVICES + " (" +
                        SERVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        SERVICE_NAME + " TEXT, " +
                        SERVICE_PRICE + " REAL, " +
                        SERVICE_DURATION + " INTEGER, " +
                        SERVICE_DESCRIPTION + " TEXT, " +
                        SERVICE_DURATION_START_TIME + " TEXT, " +
                        SERVICE_DURATION_END_TIME + " TEXT" +
                        ");";

        db.execSQL(createServices);


        String createReservations = "CREATE TABLE " + TABLE_RESERVATIONS + " (" +
                RESERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RESERVATION_CUSTOMER_ID + " INTEGER, " +
                RESERVATION_DATE + " TEXT, " +
                RESERVATION_RESPOND + " TEXT, " +
                RESERVATION_TYPE + " TEXT, " +
                RESERVATION_STATUS + " TEXT DEFAULT 'Pending', " +
                "FOREIGN KEY(" + RESERVATION_CUSTOMER_ID + ") REFERENCES " + TABLE_USERS + "(" + USER_ID + "))";
        db.execSQL(createReservations);

        // Indexes
        db.execSQL("CREATE INDEX idx_users_email ON " + TABLE_USERS + "(" + USER_EMAIL + ")");
        db.execSQL("CREATE INDEX idx_users_username ON " + TABLE_USERS + "(" + USER_USERNAME + ")");
        db.execSQL("CREATE INDEX idx_rooms_status ON " + TABLE_ROOMS + "(" + ROOM_STATUS + ")");
        db.execSQL("CREATE INDEX idx_bookings_date ON " + TABLE_BOOKINGS + "(" + BOOKING_DATE + ")");
        db.execSQL("CREATE INDEX idx_reservations_status ON " + TABLE_RESERVATIONS + "(" + RESERVATION_STATUS + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
        onCreate(db);
    }

    // -------------------- PASSWORD HASHING --------------------
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            Log.e(TAG, "Password hashing error", e);
            return password;
        }
    }

    // -------------------- USERS --------------------
    public boolean insertUser(String username, String email, String password, String profilePath,
                              String fname, String lname, String address, String contact) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(USER_USERNAME, username);
            cv.put(USER_EMAIL, email);
            cv.put(USER_PASSWORD, hashPassword(password));
            cv.put(USER_PROFILE_PATH, profilePath);
            cv.put(USER_FNAME, fname);
            cv.put(USER_LNAME, lname);
            cv.put(USER_ADDRESS, address);
            cv.put(USER_CONTACT, contact);

            long result = db.insert(TABLE_USERS, null, cv);
            if (result == -1) {
                Log.e(TAG, "Insert failed for user: " + username);
                return false;
            }
            return true;
        }
    }

    public Cursor getUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String hashed = hashPassword(password);
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                USER_EMAIL + "=? AND " + USER_PASSWORD + "=?", new String[]{email, hashed});
    }

    public boolean updateUser(int id, String fname, String lname, String address, String contact, String profilePath) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(USER_FNAME, fname);
            cv.put(USER_LNAME, lname);
            cv.put(USER_ADDRESS, address);
            cv.put(USER_CONTACT, contact);
            cv.put(USER_PROFILE_PATH, profilePath);
            return db.update(TABLE_USERS, cv, USER_ID + "=?", new String[]{String.valueOf(id)}) > 0;
        }
    }

    public boolean deleteUser(int id) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            return db.delete(TABLE_USERS, USER_ID + "=?", new String[]{String.valueOf(id)}) > 0;
        }
    }

    // -------------------- ROOMS --------------------
    // Insert room with description
    public boolean insertRoom(String name, String imagePath, String category, String description, double price, int status) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(ROOM_NAME, name);
            cv.put(ROOM_IMAGE_PATH, imagePath);
            cv.put(ROOM_CATEGORY, category);
            cv.put(ROOM_DESCRIPTION, description);   // <-- add description
            cv.put(ROOM_PRICE, price);
            cv.put(ROOM_STATUS, status);
            return db.insert(TABLE_ROOMS, null, cv) != -1;
        }
    }

    // Update room with description
    public boolean updateRoom(int roomID, String name, String imagePath, String category, String description, double price, int status) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(ROOM_NAME, name);
            cv.put(ROOM_IMAGE_PATH, imagePath);
            cv.put(ROOM_CATEGORY, category);
            cv.put(ROOM_DESCRIPTION, description);  // <-- add description
            cv.put(ROOM_PRICE, price);
            cv.put(ROOM_STATUS, status);
            return db.update(TABLE_ROOMS, cv, ROOM_ID + "=?", new String[]{String.valueOf(roomID)}) > 0;
        }
    }


    public Cursor getAllRooms() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ROOMS, null);
    }

    public boolean updateRoomStatus(int roomID, int status) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(ROOM_STATUS, status);
            return db.update(TABLE_ROOMS, cv, ROOM_ID + "=?", new String[]{String.valueOf(roomID)}) > 0;
        }
    }

    public boolean deleteRoom(int roomID) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            return db.delete(TABLE_ROOMS, ROOM_ID + "=?", new String[]{String.valueOf(roomID)}) > 0;
        }
    }

    // -------------------- BOOKINGS --------------------
    public boolean insertBooking(int roomID, int customerID, String category, double price, String date) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(BOOKING_ROOM_ID, roomID);
            cv.put(BOOKING_CUSTOMER_ID, customerID);
            cv.put(BOOKING_CATEGORY, category);
            cv.put(BOOKING_PRICE, price);
            cv.put(BOOKING_DATE, date);
            return db.insert(TABLE_BOOKINGS, null, cv) != -1;
        }
    }

    public boolean deleteBooking(int bookingID) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            return db.delete(TABLE_BOOKINGS, BOOKING_ID + "=?", new String[]{String.valueOf(bookingID)}) > 0;
        }
    }

    // -------------------- SERVICES --------------------

    // ----------------- INSERT SERVICE -----------------
    public boolean insertService(String name, double price, int duration, String description, String startTime, String endTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SERVICE_NAME, name); // Service Name
        cv.put(SERVICE_PRICE, price); // Price
        cv.put(SERVICE_DURATION, duration); // Duration in minutes
        cv.put(SERVICE_DESCRIPTION, description); // Description
        cv.put(SERVICE_DURATION_START_TIME, startTime); // Start Time (HH:mm)
        cv.put(SERVICE_DURATION_END_TIME, endTime); // End Time (HH:mm)



        long result = db.insert(TABLE_SERVICES, null, cv);
        return result != -1; // Return true if insert succeeded
    }

    // ----------------- GET ALL SERVICES -----------------
    public Cursor getAllServices() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SERVICES, null);
    }

    // ----------------- UPDATE SERVICE -----------------
    public boolean updateService(int id, String name, double price, int duration, String description, String startTime, String endTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SERVICE_NAME, name);
        cv.put(SERVICE_PRICE, price);
        cv.put(SERVICE_DURATION, duration);
        cv.put(SERVICE_DESCRIPTION, description);
        cv.put(SERVICE_DURATION_START_TIME, startTime);
        cv.put(SERVICE_DURATION_START_TIME, endTime);

        int rows = db.update(TABLE_SERVICES, cv, SERVICE_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    // ----------------- DELETE SERVICE -----------------
    public boolean deleteService(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_SERVICES, SERVICE_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }



    // -------------------- RESERVATIONS --------------------
    public long insertReservation(int customerID, String date, String respond, String type) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(RESERVATION_CUSTOMER_ID, customerID);
            cv.put(RESERVATION_DATE, date);
            cv.put(RESERVATION_RESPOND, respond);
            cv.put(RESERVATION_TYPE, type);
            return db.insert(TABLE_RESERVATIONS, null, cv);
        }
    }

    public boolean updateReservationStatus(int reservationID, String status) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(RESERVATION_STATUS, status);
            return db.update(TABLE_RESERVATIONS, cv, RESERVATION_ID + "=?", new String[]{String.valueOf(reservationID)}) > 0;
        }
    }

    public boolean deleteReservation(int reservationID) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            return db.delete(TABLE_RESERVATIONS, RESERVATION_ID + "=?", new String[]{String.valueOf(reservationID)}) > 0;
        }
    }

    public Cursor getAllReservations() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RESERVATIONS, null);
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + USER_EMAIL + "=?", new String[]{email});
    }

    //other curd oprations

    // -------------------- USERS --------------------
    public Cursor searchUser(String keyword) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS +
                        " WHERE " + USER_USERNAME + " LIKE ? OR " + USER_EMAIL + " LIKE ?",
                new String[]{"%" + keyword + "%", "%" + keyword + "%"});
    }

    // -------------------- ROOMS --------------------
    public Cursor searchRoom(String keyword) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ROOMS +
                        " WHERE " + ROOM_NAME + " LIKE ? OR " + ROOM_CATEGORY + " LIKE ?",
                new String[]{"%" + keyword + "%", "%" + keyword + "%"});
    }

    public boolean updateRoom(int roomID, String name, String imagePath, String category, double price, int status) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(ROOM_NAME, name);
            cv.put(ROOM_IMAGE_PATH, imagePath);
            cv.put(ROOM_CATEGORY, category);
            cv.put(ROOM_PRICE, price);
            cv.put(ROOM_STATUS, status);
            return db.update(TABLE_ROOMS, cv, ROOM_ID + "=?", new String[]{String.valueOf(roomID)}) > 0;
        }
    }

    // -------------------- BOOKINGS --------------------
    public Cursor searchBooking(String keyword) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BOOKINGS +
                        " WHERE " + BOOKING_CATEGORY + " LIKE ? OR " + BOOKING_DATE + " LIKE ?",
                new String[]{"%" + keyword + "%", "%" + keyword + "%"});
    }

    public boolean updateBooking(int bookingID, int roomID, int customerID, String category, double price, String date) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(BOOKING_ROOM_ID, roomID);
            cv.put(BOOKING_CUSTOMER_ID, customerID);
            cv.put(BOOKING_CATEGORY, category);
            cv.put(BOOKING_PRICE, price);
            cv.put(BOOKING_DATE, date);
            return db.update(TABLE_BOOKINGS, cv, BOOKING_ID + "=?", new String[]{String.valueOf(bookingID)}) > 0;
        }
    }

    // -------------------- SERVICES --------------------
    public Cursor searchService(String keyword) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SERVICES +
                        " WHERE " + SERVICE_NAME + " LIKE ?",
                new String[]{"%" + keyword + "%"});
    }

    public boolean updateService(int serviceID, String name, double price, int duration) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(SERVICE_NAME, name);
            cv.put(SERVICE_PRICE, price);
            cv.put(SERVICE_DURATION, duration);
            return db.update(TABLE_SERVICES, cv, SERVICE_ID + "=?", new String[]{String.valueOf(serviceID)}) > 0;
        }
    }

    // -------------------- RESERVATIONS --------------------
    public Cursor searchReservation(String keyword) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RESERVATIONS +
                        " WHERE " + RESERVATION_TYPE + " LIKE ? OR " + RESERVATION_STATUS + " LIKE ?",
                new String[]{"%" + keyword + "%", "%" + keyword + "%"});
    }

    public boolean updateReservation(int reservationID, int customerID, String date, String respond, String type, String status) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(RESERVATION_CUSTOMER_ID, customerID);
            cv.put(RESERVATION_DATE, date);
            cv.put(RESERVATION_RESPOND, respond);
            cv.put(RESERVATION_TYPE, type);
            cv.put(RESERVATION_STATUS, status);
            return db.update(TABLE_RESERVATIONS, cv, RESERVATION_ID + "=?", new String[]{String.valueOf(reservationID)}) > 0;
        }
    }
     //Get Room information
    public Cursor getRoomById(int roomID) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ROOMS + " WHERE " + ROOM_ID + "=?", new String[]{String.valueOf(roomID)});
    }

    // Inside LuxeDB.java
    public List<RoomModel> getAllRoomList() {
        List<RoomModel> list = new ArrayList<>();
        Cursor cursor = getAllRooms(); // your existing method
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(ROOM_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ROOM_NAME));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(ROOM_IMAGE_PATH));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(ROOM_CATEGORY));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(ROOM_DESCRIPTION));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ROOM_PRICE));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(ROOM_STATUS));
                list.add(new RoomModel(id, name, image, category, desc, price, status));
            } while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // Returns number of bookings for a given room
    public int getBookingCountForRoom(int roomID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Bookings WHERE RoomID = ?",
                new String[]{String.valueOf(roomID)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public Cursor getBookingsForUser(int customerID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                "b." + BOOKING_ID + ", " +
                "r." + ROOM_NAME + ", " +
                "r." + ROOM_PRICE + ", " +
                "u." + USER_USERNAME + ", " +
                "b." + BOOKING_CATEGORY + ", " +
                "b." + BOOKING_DATE +
                " FROM " + TABLE_BOOKINGS + " AS b" +
                " INNER JOIN " + TABLE_ROOMS + " AS r" +
                " ON b." + BOOKING_ROOM_ID + " = r." + ROOM_ID +
                " INNER JOIN " + TABLE_USERS + " AS u" +
                " ON b." + BOOKING_CUSTOMER_ID + " = u." + USER_ID +
                " WHERE b." + BOOKING_CUSTOMER_ID + " = ?";

        return db.rawQuery(query, new String[]{String.valueOf(customerID)});
    }
    public Cursor getReservationsForUser(int customerID) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_RESERVATIONS + " WHERE " + RESERVATION_CUSTOMER_ID + "=?",
                new String[]{String.valueOf(customerID)}
        );
    }

//Corrected load user for user management

    // -------------------- USERS --------------------
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " ORDER BY " + USER_ID + " DESC", null);
    }


    private void loadUsers(String keyword) {
        userList.clear();
        Cursor c;

        if (keyword == null || keyword.trim().isEmpty()) {
            c = dbHelper.getAllUsers();   // <-- fetch all users
        } else {
            c = dbHelper.searchUser(keyword); // <-- filtered search
        }

        Log.d("DB_DEBUG", "Rows found: " + c.getCount());

        if (c.moveToFirst()) {
            do {
                UserModel u = new UserModel(
                        c.getInt(c.getColumnIndexOrThrow(LuxeDB.USER_ID)),
                        c.getString(c.getColumnIndexOrThrow(LuxeDB.USER_USERNAME)),
                        c.getString(c.getColumnIndexOrThrow(LuxeDB.USER_EMAIL)),
                        c.getString(c.getColumnIndexOrThrow(LuxeDB.USER_PROFILE_PATH)),
                        c.getString(c.getColumnIndexOrThrow(LuxeDB.USER_FNAME)),
                        c.getString(c.getColumnIndexOrThrow(LuxeDB.USER_LNAME)),
                        c.getString(c.getColumnIndexOrThrow(LuxeDB.USER_ADDRESS)),
                        c.getString(c.getColumnIndexOrThrow(LuxeDB.USER_CONTACT))
                );
                userList.add(u);
            } while (c.moveToNext());
        }
        c.close();

        adapter.updateData(userList);
    }







}
