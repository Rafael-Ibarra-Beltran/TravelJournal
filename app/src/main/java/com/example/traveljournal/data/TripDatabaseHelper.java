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
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_TRIPS = "trips";
    private static final String TABLE_TRIP_IMAGES = "trip_images";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TRIP_ID = "trip_id";
    private static final String COLUMN_PLACE_NAME = "place_name";
    private static final String COLUMN_TRIP_DATE = "trip_date";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_FAVORITE = "is_favorite";
    private static final String COLUMN_COMPANIONS = "companions";
    private static final String COLUMN_BUDGET = "budget";
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
                + COLUMN_CATEGORY + " TEXT NOT NULL DEFAULT 'Otro', "
                + COLUMN_FAVORITE + " INTEGER NOT NULL DEFAULT 0, "
                + COLUMN_COMPANIONS + " TEXT, "
                + COLUMN_BUDGET + " REAL NOT NULL DEFAULT 0, "
                + COLUMN_IMAGE_URI + " TEXT, "
                + COLUMN_CREATED_AT + " TEXT, "
                + COLUMN_UPDATED_AT + " TEXT"
                + ")");
        createTripImagesTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_TRIPS + " ADD COLUMN "
                    + COLUMN_CATEGORY + " TEXT NOT NULL DEFAULT 'Otro'");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_TRIPS + " ADD COLUMN "
                    + COLUMN_FAVORITE + " INTEGER NOT NULL DEFAULT 0");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_TRIPS + " ADD COLUMN "
                    + COLUMN_COMPANIONS + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_TRIPS + " ADD COLUMN "
                    + COLUMN_BUDGET + " REAL NOT NULL DEFAULT 0");
            createTripImagesTable(db);
        }
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
        db.delete(TABLE_TRIP_IMAGES, COLUMN_TRIP_ID + " = ?", new String[]{String.valueOf(id)});
        return db.delete(TABLE_TRIPS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<String> getTripImages(long tripId) {
        List<String> imageUris = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_TRIP_IMAGES, new String[]{COLUMN_IMAGE_URI}, COLUMN_TRIP_ID + " = ?",
                new String[]{String.valueOf(tripId)}, null, null, COLUMN_ID + " ASC")) {
            while (cursor.moveToNext()) {
                imageUris.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI)));
            }
        }
        return imageUris;
    }

    public void replaceTripImages(long tripId, List<String> imageUris) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TRIP_IMAGES, COLUMN_TRIP_ID + " = ?", new String[]{String.valueOf(tripId)});
        int limit = Math.min(imageUris.size(), 3);
        for (int i = 0; i < limit; i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TRIP_ID, tripId);
            values.put(COLUMN_IMAGE_URI, imageUris.get(i));
            values.put(COLUMN_CREATED_AT, now());
            db.insert(TABLE_TRIP_IMAGES, null, values);
        }
    }

    private ContentValues toValues(Trip trip) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLACE_NAME, trip.getPlaceName());
        values.put(COLUMN_TRIP_DATE, trip.getTripDate());
        values.put(COLUMN_DESCRIPTION, trip.getDescription());
        values.put(COLUMN_RATING, trip.getRating());
        values.put(COLUMN_CATEGORY, trip.getCategory());
        values.put(COLUMN_FAVORITE, trip.isFavorite() ? 1 : 0);
        values.put(COLUMN_COMPANIONS, trip.getCompanions());
        values.put(COLUMN_BUDGET, trip.getBudget());
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
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE)) == 1,
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPANIONS)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BUDGET)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))
        );
    }

    private void createTripImagesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRIP_IMAGES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TRIP_ID + " INTEGER NOT NULL, "
                + COLUMN_IMAGE_URI + " TEXT NOT NULL, "
                + COLUMN_CREATED_AT + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_TRIP_ID + ") REFERENCES " + TABLE_TRIPS + "(" + COLUMN_ID + ")"
                + ")");
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}
