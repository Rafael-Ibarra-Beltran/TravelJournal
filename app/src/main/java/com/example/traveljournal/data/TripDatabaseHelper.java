package com.example.traveljournal.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.traveljournal.model.Trip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "travel_journal.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TRIPS = "trips";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PLACE_NAME = "place_name";
    private static final String COLUMN_TRIP_DATE = "trip_date";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_IMAGE_URI = "image_uri";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_UPDATED_AT = "updated_at";

    public TripDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_TRIPS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PLACE_NAME + " TEXT NOT NULL, "
                + COLUMN_TRIP_DATE + " TEXT NOT NULL, "
                + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + COLUMN_RATING + " INTEGER NOT NULL, "
                + COLUMN_IMAGE_URI + " TEXT, "
                + COLUMN_CREATED_AT + " TEXT, "
                + COLUMN_UPDATED_AT + " TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
        onCreate(db);
    }

    public long insertTrip(Trip trip) {
        String now = now();
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = toValues(trip);
        values.put(COLUMN_CREATED_AT, now);
        values.put(COLUMN_UPDATED_AT, now);
        return db.insert(TABLE_TRIPS, null, values);
    }

    public List<Trip> getAllTrips() {
        List<Trip> trips = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_TRIPS, null, null, null, null, null,
                COLUMN_UPDATED_AT + " DESC, " + COLUMN_ID + " DESC")) {
            while (cursor.moveToNext()) {
                trips.add(fromCursor(cursor));
            }
        }
        return trips;
    }

    public Trip getTripById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_TRIPS, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (cursor.moveToFirst()) {
                return fromCursor(cursor);
            }
        }
        return null;
    }

    public int updateTrip(Trip trip) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = toValues(trip);
        values.put(COLUMN_UPDATED_AT, now());
        return db.update(TABLE_TRIPS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(trip.getId())});
    }

    public int deleteTrip(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_TRIPS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    private ContentValues toValues(Trip trip) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLACE_NAME, trip.getPlaceName());
        values.put(COLUMN_TRIP_DATE, trip.getTripDate());
        values.put(COLUMN_DESCRIPTION, trip.getDescription());
        values.put(COLUMN_RATING, trip.getRating());
        values.put(COLUMN_IMAGE_URI, trip.getImageUri());
        return values;
    }

    private Trip fromCursor(Cursor cursor) {
        return new Trip(
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRIP_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RATING)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))
        );
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}
