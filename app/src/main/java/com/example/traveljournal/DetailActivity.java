package com.example.traveljournal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.ArrayAdapter;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    private static final String STATE_PLACE = "state_place";
    private static final String STATE_DATE = "state_date";
    private static final String STATE_DESCRIPTION = "state_description";
    private static final String STATE_RATING = "state_rating";
    private static final String STATE_CATEGORY = "state_category";
    private static final String STATE_FAVORITE = "state_favorite";
    private static final String STATE_COMPANIONS = "state_companions";
    private static final String STATE_BUDGET = "state_budget";
    private static final String STATE_IMAGE_URI = "state_image_uri";
    private static final String STATE_EXTRA_IMAGES = "state_extra_images";
    private static final String STATE_SCROLL_Y = "state_scroll_y";
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String LEGACY_DATE_FORMAT = "yyyy-MM-dd";
    private static final String[] CATEGORIES = {"Ciudad", "Playa", "Montaña", "Comida", "Cultura", "Naturaleza", "Otro"};

    private TripDatabaseHelper databaseHelper;
    private long tripId = -1L;
    private String imageUri;

    private TextView detailTitleText;
    private EditText placeNameEditText;
    private EditText dateEditText;
    private EditText descriptionEditText;
    private EditText companionsEditText;
    private EditText budgetEditText;
    private RatingBar ratingBar;
    private Spinner categorySpinner;
    private CheckBox favoriteCheckBox;
    private ImageView detailImageView;
    private Button selectImageButton;
    private TextView removeMainImageButton;
    private Button addGalleryPhotoButton;
    private FrameLayout gallerySlotOne;
    private FrameLayout gallerySlotTwo;
    private FrameLayout gallerySlotThree;
    private ImageView galleryImageOne;
    private ImageView galleryImageTwo;
    private ImageView galleryImageThree;
    private TextView galleryRemoveOne;
    private TextView galleryRemoveTwo;
    private TextView galleryRemoveThree;
    private Button deleteButton;
    private ScrollView detailRoot;
    private final ArrayList<String> extraImageUris = new ArrayList<>();
    private int pendingScrollY = 0;

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

    private final ActivityResultLauncher<String[]> extraImagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null) {
                    return;
                }
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException ignored) {
                    // Some document providers do not expose persistable permissions.
                }
                if (extraImageUris.size() >= 3) {
                    showAppToast(R.string.gallery_limit_reached);
                    return;
                }
                extraImageUris.add(uri.toString());
                showExtraImages();
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
        setupCategorySpinner();
        bindActions();

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else if (tripId != -1L) {
            loadTrip();
        }
        updateMode();
        restoreScrollIfNeeded();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_PLACE, placeNameEditText.getText().toString());
        outState.putString(STATE_DATE, dateEditText.getText().toString());
        outState.putString(STATE_DESCRIPTION, descriptionEditText.getText().toString());
        outState.putFloat(STATE_RATING, ratingBar.getRating());
        outState.putInt(STATE_CATEGORY, categorySpinner.getSelectedItemPosition());
        outState.putBoolean(STATE_FAVORITE, favoriteCheckBox.isChecked());
        outState.putString(STATE_COMPANIONS, companionsEditText.getText().toString());
        outState.putString(STATE_BUDGET, budgetEditText.getText().toString());
        outState.putString(STATE_IMAGE_URI, imageUri);
        outState.putStringArrayList(STATE_EXTRA_IMAGES, extraImageUris);
        outState.putInt(STATE_SCROLL_Y, detailRoot.getScrollY());
    }

    private void bindViews() {
        detailRoot = findViewById(R.id.detailRoot);
        detailTitleText = findViewById(R.id.detailTitleText);
        placeNameEditText = findViewById(R.id.placeNameEditText);
        dateEditText = findViewById(R.id.dateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        companionsEditText = findViewById(R.id.companionsEditText);
        budgetEditText = findViewById(R.id.budgetEditText);
        ratingBar = findViewById(R.id.ratingBar);
        categorySpinner = findViewById(R.id.categorySpinner);
        favoriteCheckBox = findViewById(R.id.favoriteCheckBox);
        detailImageView = findViewById(R.id.detailImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        removeMainImageButton = findViewById(R.id.removeMainImageButton);
        addGalleryPhotoButton = findViewById(R.id.addGalleryPhotoButton);
        gallerySlotOne = findViewById(R.id.gallerySlotOne);
        gallerySlotTwo = findViewById(R.id.gallerySlotTwo);
        gallerySlotThree = findViewById(R.id.gallerySlotThree);
        galleryImageOne = findViewById(R.id.galleryImageOne);
        galleryImageTwo = findViewById(R.id.galleryImageTwo);
        galleryImageThree = findViewById(R.id.galleryImageThree);
        galleryRemoveOne = findViewById(R.id.galleryRemoveOne);
        galleryRemoveTwo = findViewById(R.id.galleryRemoveTwo);
        galleryRemoveThree = findViewById(R.id.galleryRemoveThree);
        deleteButton = findViewById(R.id.deleteButton);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CATEGORIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void bindActions() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        selectImageButton.setOnClickListener(v -> imagePickerLauncher.launch(new String[]{"image/*"}));
        removeMainImageButton.setOnClickListener(v -> removeMainImage());
        addGalleryPhotoButton.setOnClickListener(v -> addGalleryPhoto());
        dateEditText.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.saveButton).setOnClickListener(v -> saveTrip());
        deleteButton.setOnClickListener(v -> confirmDelete());
    }

    private void restoreState(Bundle savedInstanceState) {
        placeNameEditText.setText(savedInstanceState.getString(STATE_PLACE, ""));
        dateEditText.setText(savedInstanceState.getString(STATE_DATE, ""));
        descriptionEditText.setText(savedInstanceState.getString(STATE_DESCRIPTION, ""));
        ratingBar.setRating(savedInstanceState.getFloat(STATE_RATING, 0f));
        categorySpinner.setSelection(savedInstanceState.getInt(STATE_CATEGORY, CATEGORIES.length - 1));
        favoriteCheckBox.setChecked(savedInstanceState.getBoolean(STATE_FAVORITE, false));
        companionsEditText.setText(savedInstanceState.getString(STATE_COMPANIONS, ""));
        budgetEditText.setText(savedInstanceState.getString(STATE_BUDGET, ""));
        imageUri = savedInstanceState.getString(STATE_IMAGE_URI);
        ArrayList<String> restoredExtraImages = savedInstanceState.getStringArrayList(STATE_EXTRA_IMAGES);
        if (restoredExtraImages != null) {
            extraImageUris.clear();
            extraImageUris.addAll(restoredExtraImages);
        }
        pendingScrollY = savedInstanceState.getInt(STATE_SCROLL_Y, 0);
        showImage();
        showExtraImages();
    }

    private void restoreScrollIfNeeded() {
        if (pendingScrollY <= 0) {
            return;
        }
        detailRoot.post(() -> detailRoot.scrollTo(0, pendingScrollY));
    }

    private void loadTrip() {
        Trip trip = databaseHelper.getTripById(tripId);
        if (trip == null) {
            showAppToast(R.string.empty_trips);
            finish();
            return;
        }
        placeNameEditText.setText(trip.getPlaceName());
        dateEditText.setText(trip.getTripDate());
        descriptionEditText.setText(trip.getDescription());
        ratingBar.setRating(trip.getRating());
        categorySpinner.setSelection(categoryToPosition(trip.getCategory()));
        favoriteCheckBox.setChecked(trip.isFavorite());
        companionsEditText.setText(trip.getCompanions());
        budgetEditText.setText(trip.getBudget() > 0 ? String.format(Locale.US, "%.2f", trip.getBudget()) : "");
        imageUri = trip.getImageUri();
        extraImageUris.clear();
        extraImageUris.addAll(databaseHelper.getTripImages(tripId));
        showImage();
        showExtraImages();
    }

    private void updateMode() {
        boolean existingTrip = tripId != -1L;
        detailTitleText.setText(existingTrip ? R.string.edit_trip : R.string.new_trip);
        deleteButton.setVisibility(existingTrip ? View.VISIBLE : View.GONE);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        String currentDate = dateEditText.getText().toString();
        if (!TextUtils.isEmpty(currentDate)) {
            try {
                calendar.setTime(parseDate(currentDate));
            } catch (ParseException ignored) {
                // If the stored date is unexpected, fall back to today.
            }
        }

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> dateEditText.setText(String.format(Locale.getDefault(),
                        "%02d/%02d/%04d", dayOfMonth, month + 1, year)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private java.util.Date parseDate(String value) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        formatter.setLenient(false);
        try {
            return formatter.parse(value);
        } catch (ParseException ignored) {
            SimpleDateFormat legacyFormatter = new SimpleDateFormat(LEGACY_DATE_FORMAT, Locale.getDefault());
            legacyFormatter.setLenient(false);
            return legacyFormatter.parse(value);
        }
    }

    private void showImage() {
        if (TextUtils.isEmpty(imageUri)) {
            detailImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            detailImageView.setImageResource(R.drawable.ic_image_placeholder);
            selectImageButton.setVisibility(View.VISIBLE);
            removeMainImageButton.setVisibility(View.GONE);
            return;
        }
        try {
            detailImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            detailImageView.setImageURI(Uri.parse(imageUri));
            selectImageButton.setVisibility(View.GONE);
            removeMainImageButton.setVisibility(View.VISIBLE);
        } catch (RuntimeException ex) {
            detailImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            detailImageView.setImageResource(R.drawable.ic_image_placeholder);
            selectImageButton.setVisibility(View.VISIBLE);
            removeMainImageButton.setVisibility(View.GONE);
            showAppToast(R.string.image_error);
        }
    }

    private void removeMainImage() {
        imageUri = null;
        showImage();
    }

    private void addGalleryPhoto() {
        if (extraImageUris.size() >= 3) {
            showAppToast(R.string.gallery_limit_reached);
            return;
        }
        extraImagePickerLauncher.launch(new String[]{"image/*"});
    }

    private void showExtraImages() {
        showGalleryImage(gallerySlotOne, galleryImageOne, galleryRemoveOne, 0);
        showGalleryImage(gallerySlotTwo, galleryImageTwo, galleryRemoveTwo, 1);
        showGalleryImage(gallerySlotThree, galleryImageThree, galleryRemoveThree, 2);
        if (addGalleryPhotoButton != null) {
            addGalleryPhotoButton.setVisibility(extraImageUris.size() >= 3 ? View.GONE : View.VISIBLE);
        }
    }

    private void showGalleryImage(FrameLayout slot, ImageView imageView, TextView removeView, int index) {
        if (slot == null || imageView == null || removeView == null) {
            return;
        }
        if (index >= extraImageUris.size()) {
            slot.setVisibility(View.GONE);
            imageView.setOnClickListener(null);
            removeView.setOnClickListener(null);
            return;
        }
        slot.setVisibility(View.VISIBLE);
        try {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageURI(Uri.parse(extraImageUris.get(index)));
        } catch (RuntimeException ex) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageResource(R.drawable.ic_image_placeholder);
        }
        imageView.setOnClickListener(v -> {
            imageUri = extraImageUris.get(index);
            showImage();
        });
        removeView.setOnClickListener(v -> {
            extraImageUris.remove(index);
            showExtraImages();
        });
    }

    private void saveTrip() {
        if (!validateForm()) {
            return;
        }

        Trip trip = readTripFromForm();
        if (tripId == -1L) {
            long newId = databaseHelper.insertTrip(trip);
            if (newId != -1L) {
                databaseHelper.replaceTripImages(newId, extraImageUris);
                showAppToast(R.string.trip_saved);
                finish();
            } else {
                showAppToast(R.string.image_error);
            }
        } else {
            trip.setId(tripId);
            if (databaseHelper.updateTrip(trip) > 0) {
                databaseHelper.replaceTripImages(tripId, extraImageUris);
                showAppToast(R.string.trip_updated);
                finish();
            } else {
                showAppToast(R.string.image_error);
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
        } else if (!isValidPastOrTodayDate(dateEditText.getText().toString().trim())) {
            valid = false;
        }
        if (TextUtils.isEmpty(descriptionEditText.getText().toString().trim())) {
            descriptionEditText.setError(getString(R.string.required_field));
            valid = false;
        }
        if (ratingBar.getRating() < 1f || ratingBar.getRating() > 5f) {
            showAppToast(R.string.rating_required);
            valid = false;
        }
        if (!isValidBudget()) {
            valid = false;
        }
        return valid;
    }

    private boolean isValidBudget() {
        String value = budgetEditText.getText().toString().trim();
        if (TextUtils.isEmpty(value)) {
            return true;
        }
        try {
            if (Double.parseDouble(value) < 0) {
                budgetEditText.setError(getString(R.string.invalid_budget_error));
                return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            budgetEditText.setError(getString(R.string.invalid_budget_error));
            return false;
        }
    }

    private Trip readTripFromForm() {
        Trip trip = new Trip();
        trip.setPlaceName(placeNameEditText.getText().toString().trim());
        trip.setTripDate(dateEditText.getText().toString().trim());
        trip.setDescription(descriptionEditText.getText().toString().trim());
        trip.setRating(Math.round(ratingBar.getRating()));
        trip.setCategory(categorySpinner.getSelectedItem().toString());
        trip.setFavorite(favoriteCheckBox.isChecked());
        trip.setCompanions(companionsEditText.getText().toString().trim());
        trip.setBudget(readBudget());
        trip.setImageUri(imageUri);
        return trip;
    }

    private double readBudget() {
        String value = budgetEditText.getText().toString().trim();
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        return Double.parseDouble(value);
    }

    private boolean isValidPastOrTodayDate(String value) {
        try {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTime(parseDate(value));
            clearTime(selectedDate);

            Calendar today = Calendar.getInstance();
            clearTime(today);
            if (selectedDate.after(today)) {
                dateEditText.setError(getString(R.string.future_date_error));
                return false;
            }
            return true;
        } catch (ParseException ex) {
            dateEditText.setError(getString(R.string.invalid_date_error));
            return false;
        }
    }

    private void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private int categoryToPosition(String category) {
        for (int i = 0; i < CATEGORIES.length; i++) {
            if (CATEGORIES[i].equals(category)) {
                return i;
            }
        }
        return CATEGORIES.length - 1;
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.travel_journal_logo)
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
            showAppToast(R.string.trip_deleted);
            finish();
        }
    }

    @SuppressWarnings("deprecation")
    private void showAppToast(int messageResId) {
        ViewGroup root = findViewById(android.R.id.content);
        View toastView = getLayoutInflater().inflate(R.layout.toast_message, root, false);
        TextView toastMessageText = toastView.findViewById(R.id.toastMessageText);
        toastMessageText.setText(messageResId);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.show();
    }
}
