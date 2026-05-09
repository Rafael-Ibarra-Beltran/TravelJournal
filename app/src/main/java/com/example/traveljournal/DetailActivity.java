package com.example.traveljournal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.traveljournal.data.TripDatabaseHelper;
import com.example.traveljournal.model.Trip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    private static final String STATE_PLACE = "state_place";
    private static final String STATE_DATE = "state_date";
    private static final String STATE_DESCRIPTION = "state_description";
    private static final String STATE_RATING = "state_rating";
    private static final String STATE_IMAGE_URI = "state_image_uri";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private TripDatabaseHelper databaseHelper;
    private long tripId = -1L;
    private String imageUri;

    private TextView detailTitleText;
    private EditText placeNameEditText;
    private EditText dateEditText;
    private EditText descriptionEditText;
    private RatingBar ratingBar;
    private ImageView detailImageView;
    private Button shareButton;
    private Button deleteButton;

    private final ActivityResultLauncher<String[]> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null) {
                    return;
                }
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException ignored) {
                    // Some document providers do not expose persistable permissions.
                }
                imageUri = uri.toString();
                showImage();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    v.getPaddingLeft() + systemBars.left,
                    v.getPaddingTop() + systemBars.top,
                    v.getPaddingRight() + systemBars.right,
                    v.getPaddingBottom() + systemBars.bottom
            );
            return insets;
        });

        databaseHelper = new TripDatabaseHelper(this);
        tripId = getIntent().getLongExtra(MainActivity.EXTRA_TRIP_ID, -1L);
        bindViews();
        bindActions();

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else if (tripId != -1L) {
            loadTrip();
        }
        updateMode();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_PLACE, placeNameEditText.getText().toString());
        outState.putString(STATE_DATE, dateEditText.getText().toString());
        outState.putString(STATE_DESCRIPTION, descriptionEditText.getText().toString());
        outState.putFloat(STATE_RATING, ratingBar.getRating());
        outState.putString(STATE_IMAGE_URI, imageUri);
    }

    private void bindViews() {
        detailTitleText = findViewById(R.id.detailTitleText);
        placeNameEditText = findViewById(R.id.placeNameEditText);
        dateEditText = findViewById(R.id.dateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        ratingBar = findViewById(R.id.ratingBar);
        detailImageView = findViewById(R.id.detailImageView);
        shareButton = findViewById(R.id.shareButton);
        deleteButton = findViewById(R.id.deleteButton);
    }

    private void bindActions() {
        findViewById(R.id.selectImageButton).setOnClickListener(v -> imagePickerLauncher.launch(new String[]{"image/*"}));
        dateEditText.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.saveButton).setOnClickListener(v -> saveTrip());
        shareButton.setOnClickListener(v -> shareTrip());
        deleteButton.setOnClickListener(v -> confirmDelete());
    }

    private void restoreState(Bundle savedInstanceState) {
        placeNameEditText.setText(savedInstanceState.getString(STATE_PLACE, ""));
        dateEditText.setText(savedInstanceState.getString(STATE_DATE, ""));
        descriptionEditText.setText(savedInstanceState.getString(STATE_DESCRIPTION, ""));
        ratingBar.setRating(savedInstanceState.getFloat(STATE_RATING, 0f));
        imageUri = savedInstanceState.getString(STATE_IMAGE_URI);
        showImage();
    }

    private void loadTrip() {
        Trip trip = databaseHelper.getTripById(tripId);
        if (trip == null) {
            Toast.makeText(this, R.string.empty_trips, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        placeNameEditText.setText(trip.getPlaceName());
        dateEditText.setText(trip.getTripDate());
        descriptionEditText.setText(trip.getDescription());
        ratingBar.setRating(trip.getRating());
        imageUri = trip.getImageUri();
        showImage();
    }

    private void updateMode() {
        boolean existingTrip = tripId != -1L;
        detailTitleText.setText(existingTrip ? R.string.edit_trip : R.string.new_trip);
        shareButton.setVisibility(existingTrip ? View.VISIBLE : View.GONE);
        deleteButton.setVisibility(existingTrip ? View.VISIBLE : View.GONE);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        String currentDate = dateEditText.getText().toString();
        if (!TextUtils.isEmpty(currentDate)) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                calendar.setTime(formatter.parse(currentDate));
            } catch (ParseException ignored) {
                // If the stored date is unexpected, fall back to today.
            }
        }

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> dateEditText.setText(String.format(Locale.getDefault(),
                        "%04d-%02d-%02d", year, month + 1, dayOfMonth)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void showImage() {
        if (TextUtils.isEmpty(imageUri)) {
            detailImageView.setImageDrawable(null);
            return;
        }
        try {
            detailImageView.setImageURI(Uri.parse(imageUri));
        } catch (RuntimeException ex) {
            detailImageView.setImageDrawable(null);
            Toast.makeText(this, R.string.image_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTrip() {
        if (!validateForm()) {
            return;
        }

        Trip trip = readTripFromForm();
        if (tripId == -1L) {
            long newId = databaseHelper.insertTrip(trip);
            if (newId != -1L) {
                Toast.makeText(this, R.string.trip_saved, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.image_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            trip.setId(tripId);
            if (databaseHelper.updateTrip(trip) > 0) {
                Toast.makeText(this, R.string.trip_updated, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.image_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        if (TextUtils.isEmpty(placeNameEditText.getText().toString().trim())) {
            placeNameEditText.setError(getString(R.string.required_field));
            valid = false;
        }
        if (TextUtils.isEmpty(dateEditText.getText().toString().trim())) {
            dateEditText.setError(getString(R.string.required_field));
            valid = false;
        }
        if (TextUtils.isEmpty(descriptionEditText.getText().toString().trim())) {
            descriptionEditText.setError(getString(R.string.required_field));
            valid = false;
        }
        if (ratingBar.getRating() < 1f || ratingBar.getRating() > 5f) {
            Toast.makeText(this, R.string.rating_required, Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    private Trip readTripFromForm() {
        Trip trip = new Trip();
        trip.setPlaceName(placeNameEditText.getText().toString().trim());
        trip.setTripDate(dateEditText.getText().toString().trim());
        trip.setDescription(descriptionEditText.getText().toString().trim());
        trip.setRating(Math.round(ratingBar.getRating()));
        trip.setImageUri(imageUri);
        return trip;
    }

    private void shareTrip() {
        if (!validateForm()) {
            return;
        }
        String shareText = getString(R.string.share_title) + "\n\n"
                + "Lugar: " + placeNameEditText.getText().toString().trim() + "\n"
                + "Fecha: " + dateEditText.getText().toString().trim() + "\n"
                + "Calificación: " + Math.round(ratingBar.getRating()) + "/5\n\n"
                + "Descripción:\n" + descriptionEditText.getText().toString().trim();

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_trip)));
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.delete_trip, (dialog, which) -> deleteTrip())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteTrip() {
        if (tripId == -1L) {
            return;
        }
        if (databaseHelper.deleteTrip(tripId) > 0) {
            Toast.makeText(this, R.string.trip_deleted, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
