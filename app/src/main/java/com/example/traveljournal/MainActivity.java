package com.example.traveljournal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveljournal.data.TripDatabaseHelper;
import com.example.traveljournal.model.Trip;
import com.example.traveljournal.ui.TripAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_TRIP_ID = "com.example.traveljournal.EXTRA_TRIP_ID";
    private static final String STATE_SEARCH_QUERY = "search_query";
    private static final String STATE_RATING_FILTER = "rating_filter";
    private static final String STATE_SORT_BY = "sort_by";
    private static final String STATE_SORT_DIRECTION = "sort_direction";
    private static final String STATE_SELECTED_TRIP_ID = "selected_trip_id";
    private static final String STATE_FILTERS_EXPANDED = "filters_expanded";

    private static final int SORT_UPDATED = 0;
    private static final int SORT_PLACE = 1;
    private static final int SORT_RATING = 2;
    private static final int SORT_DATE = 3;
    private static final int DIRECTION_DESC = 0;
    private static final int DIRECTION_ASC = 1;

    private TripDatabaseHelper databaseHelper;
    private TripAdapter tripAdapter;
    private LinearLayout emptyStateLayout;
    private TextView emptyTitleTextView;
    private TextView emptyHintTextView;
    private EditText searchEditText;
    private Spinner ratingFilterSpinner;
    private Spinner sortBySpinner;
    private Spinner sortDirectionSpinner;
    private LinearLayout filterBody;
    private TextView filterToggleText;
    private final List<Trip> allTrips = new ArrayList<>();
    private View tabletDetailPanel;
    private TextView tabletEmptyDetailText;
    private TextView tabletPlaceText;
    private TextView tabletDateText;
    private TextView tabletDescriptionText;
    private TextView tabletRatingText;
    private ImageView tabletImageView;
    private Button tabletOpenButton;
    private long selectedTripId = -1L;
    private String searchQuery = "";
    private int selectedMinimumRating = 0;
    private int selectedSortBy = SORT_UPDATED;
    private int selectedSortDirection = DIRECTION_DESC;
    private boolean filtersExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new TripDatabaseHelper(this);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        emptyTitleTextView = findViewById(R.id.emptyTitleText);
        emptyHintTextView = findViewById(R.id.emptyHintText);
        RecyclerView tripsRecyclerView = findViewById(R.id.tripsRecyclerView);
        tripsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tripAdapter = new TripAdapter(this::openTrip);
        tripsRecyclerView.setAdapter(tripAdapter);

        findViewById(R.id.addTripButton).setOnClickListener(v -> openNewTrip());
        restoreState(savedInstanceState);
        setupFilterControls();
        bindTabletViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_SEARCH_QUERY, searchQuery);
        outState.putInt(STATE_RATING_FILTER, selectedMinimumRating);
        outState.putInt(STATE_SORT_BY, selectedSortBy);
        outState.putInt(STATE_SORT_DIRECTION, selectedSortDirection);
        outState.putLong(STATE_SELECTED_TRIP_ID, selectedTripId);
        outState.putBoolean(STATE_FILTERS_EXPANDED, filtersExpanded);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrips();
    }

    private void loadTrips() {
        allTrips.clear();
        allTrips.addAll(databaseHelper.getAllTrips());
        applyFilters();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        searchQuery = savedInstanceState.getString(STATE_SEARCH_QUERY, "");
        selectedMinimumRating = savedInstanceState.getInt(STATE_RATING_FILTER, 0);
        selectedSortBy = savedInstanceState.getInt(STATE_SORT_BY, SORT_UPDATED);
        selectedSortDirection = savedInstanceState.getInt(STATE_SORT_DIRECTION, DIRECTION_DESC);
        selectedTripId = savedInstanceState.getLong(STATE_SELECTED_TRIP_ID, -1L);
        filtersExpanded = savedInstanceState.getBoolean(STATE_FILTERS_EXPANDED, false);
    }

    private void setupFilterControls() {
        View filterHeader = findViewById(R.id.filterHeader);
        filterBody = findViewById(R.id.filterBody);
        filterToggleText = findViewById(R.id.filterToggleText);
        searchEditText = findViewById(R.id.searchEditText);
        ratingFilterSpinner = findViewById(R.id.ratingFilterSpinner);
        sortBySpinner = findViewById(R.id.sortBySpinner);
        sortDirectionSpinner = findViewById(R.id.sortDirectionSpinner);
        Button clearFiltersButton = findViewById(R.id.clearFiltersButton);

        setupSpinner(ratingFilterSpinner, new String[]{"Todas", "5★", "4★", "3★", "2★", "1★"});
        setupSpinner(sortBySpinner, new String[]{"Actualización", "Lugar", "Calificación", "Fecha"});
        setupSpinner(sortDirectionSpinner, new String[]{"Descendente", "Ascendente"});

        searchEditText.setText(searchQuery);
        ratingFilterSpinner.setSelection(ratingToSpinnerPosition(selectedMinimumRating));
        sortBySpinner.setSelection(selectedSortBy);
        sortDirectionSpinner.setSelection(selectedSortDirection);
        updateFiltersVisibility();
        filterHeader.setOnClickListener(v -> {
            filtersExpanded = !filtersExpanded;
            updateFiltersVisibility();
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ratingFilterSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMinimumRating = spinnerPositionToRating(position);
                applyFilters();
            }
        });
        sortBySpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSortBy = position;
                applyFilters();
            }
        });
        sortDirectionSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSortDirection = position;
                applyFilters();
            }
        });

        clearFiltersButton.setOnClickListener(v -> {
            searchEditText.setText("");
            ratingFilterSpinner.setSelection(0);
            sortBySpinner.setSelection(SORT_UPDATED);
            sortDirectionSpinner.setSelection(DIRECTION_DESC);
        });
    }

    private void setupSpinner(Spinner spinner, String[] values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void updateFiltersVisibility() {
        filterBody.setVisibility(filtersExpanded ? View.VISIBLE : View.GONE);
        filterToggleText.setText(filtersExpanded ? "⌃" : "⌄");
    }

    private void applyFilters() {
        if (tripAdapter == null) {
            return;
        }
        String normalizedSearch = searchQuery.trim().toLowerCase(Locale.getDefault());
        List<Trip> filteredTrips = new ArrayList<>();
        for (Trip trip : allTrips) {
            String placeName = trip.getPlaceName() == null ? "" : trip.getPlaceName().toLowerCase(Locale.getDefault());
            boolean matchesSearch = normalizedSearch.isEmpty() || placeName.contains(normalizedSearch);
            boolean matchesRating = selectedMinimumRating == 0 || trip.getRating() >= selectedMinimumRating;
            if (matchesSearch && matchesRating) {
                filteredTrips.add(trip);
            }
        }
        filteredTrips.sort(getTripComparator());
        if (selectedSortDirection == DIRECTION_DESC) {
            java.util.Collections.reverse(filteredTrips);
        }
        tripAdapter.submitList(filteredTrips);
        updateEmptyState(filteredTrips);
        updateTabletSelection(filteredTrips);
    }

    private Comparator<Trip> getTripComparator() {
        switch (selectedSortBy) {
            case SORT_PLACE:
                return Comparator.comparing(trip -> safeText(trip.getPlaceName()), String.CASE_INSENSITIVE_ORDER);
            case SORT_RATING:
                return Comparator.comparingInt(Trip::getRating);
            case SORT_DATE:
                return Comparator.comparing(trip -> safeText(trip.getTripDate()));
            case SORT_UPDATED:
            default:
                return Comparator.comparing(trip -> safeText(trip.getUpdatedAt()));
        }
    }

    private void updateEmptyState(List<Trip> filteredTrips) {
        boolean hasNoResults = filteredTrips.isEmpty();
        emptyStateLayout.setVisibility(hasNoResults ? View.VISIBLE : View.GONE);
        if (!hasNoResults) {
            return;
        }
        boolean hasSavedTrips = !allTrips.isEmpty();
        emptyTitleTextView.setText(hasSavedTrips ? R.string.empty_filtered_trips : R.string.empty_trips);
        emptyHintTextView.setText(hasSavedTrips ? R.string.empty_filtered_trips_hint : R.string.empty_trips_hint);
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private int ratingToSpinnerPosition(int rating) {
        if (rating <= 0) {
            return 0;
        }
        return 6 - rating;
    }

    private int spinnerPositionToRating(int position) {
        if (position <= 0) {
            return 0;
        }
        return 6 - position;
    }

    private void openNewTrip() {
        startActivity(new Intent(this, DetailActivity.class));
    }

    private void openTrip(Trip trip) {
        selectedTripId = trip.getId();
        showTabletTrip(trip);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EXTRA_TRIP_ID, trip.getId());
        startActivity(intent);
    }

    private void bindTabletViews() {
        tabletDetailPanel = findViewById(R.id.tabletDetailPanel);
        tabletEmptyDetailText = findViewById(R.id.tabletEmptyDetailText);
        tabletPlaceText = findViewById(R.id.tabletPlaceText);
        tabletDateText = findViewById(R.id.tabletDateText);
        tabletDescriptionText = findViewById(R.id.tabletDescriptionText);
        tabletRatingText = findViewById(R.id.tabletRatingText);
        tabletImageView = findViewById(R.id.tabletImageView);
        tabletOpenButton = findViewById(R.id.tabletOpenButton);
    }

    private void updateTabletSelection(List<Trip> trips) {
        if (tabletDetailPanel == null) {
            return;
        }
        Trip selectedTrip = null;
        for (Trip trip : trips) {
            if (trip.getId() == selectedTripId) {
                selectedTrip = trip;
                break;
            }
        }
        if (selectedTrip == null && !trips.isEmpty()) {
            selectedTrip = trips.get(0);
            selectedTripId = selectedTrip.getId();
        }
        showTabletTrip(selectedTrip);
    }

    private void showTabletTrip(Trip trip) {
        if (tabletDetailPanel == null) {
            return;
        }
        if (trip == null) {
            tabletDetailPanel.setVisibility(View.GONE);
            tabletEmptyDetailText.setVisibility(View.VISIBLE);
            return;
        }
        tabletDetailPanel.setVisibility(View.VISIBLE);
        tabletEmptyDetailText.setVisibility(View.GONE);
        tabletPlaceText.setText(trip.getPlaceName());
        tabletDateText.setText(trip.getTripDate());
        tabletDescriptionText.setText(trip.getDescription());
        tabletRatingText.setText("★ " + trip.getRating() + "/5");
        if (!TextUtils.isEmpty(trip.getImageUri())) {
            try {
                tabletImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                tabletImageView.setImageURI(Uri.parse(trip.getImageUri()));
            } catch (RuntimeException ignored) {
                tabletImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                tabletImageView.setImageResource(R.drawable.ic_image_placeholder);
            }
        } else {
            tabletImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            tabletImageView.setImageResource(R.drawable.ic_image_placeholder);
        }
        tabletOpenButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(EXTRA_TRIP_ID, trip.getId());
            startActivity(intent);
        });
    }

    private abstract static class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
