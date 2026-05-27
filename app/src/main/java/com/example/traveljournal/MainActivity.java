package com.example.traveljournal;

import android.content.ClipData;
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
import android.widget.CheckBox;
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
import com.example.traveljournal.util.MoneyUtils;
import com.example.traveljournal.util.TripDateUtils;
import com.example.traveljournal.util.TripShareUtils;
import com.example.traveljournal.util.TripStats;
import com.example.traveljournal.util.TripStatsCalculator;

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
    private static final String STATE_LIST_POSITION = "list_position";
    private static final String STATE_FAVORITES_ONLY = "favorites_only";
    private static final String STATE_CATEGORY_FILTER = "category_filter";
    private static final String STATE_DASHBOARD_EXPANDED = "dashboard_expanded";

    private static final int SORT_UPDATED = 0;
    private static final int SORT_PLACE = 1;
    private static final int SORT_RATING = 2;
    private static final int SORT_DATE = 3;
    private static final int DIRECTION_DESC = 0;
    private static final int DIRECTION_ASC = 1;

    private TripDatabaseHelper databaseHelper;
    private TripAdapter tripAdapter;
    private LinearLayoutManager tripsLayoutManager;
    private LinearLayout emptyStateLayout;
    private TextView emptyTitleTextView;
    private TextView emptyHintTextView;
    private TextView statsTotalText;
    private TextView statsAverageText;
    private TextView statsFavoriteText;
    private TextView statsBudgetText;
    private TextView statsBestText;
    private TextView statsLatestText;
    private TextView statsCategoryText;
    private LinearLayout dashboardExtraPanel;
    private TextView dashboardToggleText;
    private EditText searchEditText;
    private CheckBox favoriteFilterCheckBox;
    private Spinner categoryFilterSpinner;
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
    private TextView tabletCategoryText;
    private TextView tabletFavoriteText;
    private TextView tabletCompanionsText;
    private TextView tabletBudgetText;
    private TextView tabletDescriptionText;
    private TextView tabletRatingText;
    private ImageView tabletImageView;
    private LinearLayout tabletGalleryRow;
    private ImageView tabletGalleryImageOne;
    private ImageView tabletGalleryImageTwo;
    private ImageView tabletGalleryImageThree;
    private Button tabletOpenButton;
    private long selectedTripId = -1L;
    private String searchQuery = "";
    private int selectedMinimumRating = 0;
    private int selectedSortBy = SORT_UPDATED;
    private int selectedSortDirection = DIRECTION_DESC;
    private boolean favoritesOnly = false;
    private String selectedCategoryFilter = "Todas";
    private boolean dashboardExpanded = false;
    private boolean filtersExpanded = false;
    private int pendingListPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setupEdgeToEdgePadding();

        databaseHelper = new TripDatabaseHelper(this);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        emptyTitleTextView = findViewById(R.id.emptyTitleText);
        emptyHintTextView = findViewById(R.id.emptyHintText);
        statsTotalText = findViewById(R.id.statsTotalText);
        statsAverageText = findViewById(R.id.statsAverageText);
        statsFavoriteText = findViewById(R.id.statsFavoriteText);
        statsBudgetText = findViewById(R.id.statsBudgetText);
        statsBestText = findViewById(R.id.statsBestText);
        statsLatestText = findViewById(R.id.statsLatestText);
        statsCategoryText = findViewById(R.id.statsCategoryText);
        dashboardExtraPanel = findViewById(R.id.dashboardExtraPanel);
        dashboardToggleText = findViewById(R.id.dashboardToggleText);
        RecyclerView tripsRecyclerView = findViewById(R.id.tripsRecyclerView);
        tripsLayoutManager = new LinearLayoutManager(this);
        tripsRecyclerView.setLayoutManager(tripsLayoutManager);
        tripAdapter = new TripAdapter(this::openTrip, this::shareTrip);
        tripsRecyclerView.setAdapter(tripAdapter);

        restoreState(savedInstanceState);
        findViewById(R.id.addTripButton).setOnClickListener(v -> openNewTrip());
        setupDashboardControls();
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
        outState.putInt(STATE_LIST_POSITION, tripsLayoutManager.findFirstVisibleItemPosition());
        outState.putBoolean(STATE_FAVORITES_ONLY, favoritesOnly);
        outState.putString(STATE_CATEGORY_FILTER, selectedCategoryFilter);
        outState.putBoolean(STATE_DASHBOARD_EXPANDED, dashboardExpanded);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrips();
    }

    private void setupEdgeToEdgePadding() {
        View mainView = findViewById(R.id.main);
        int initialPaddingLeft = mainView.getPaddingLeft();
        int initialPaddingTop = mainView.getPaddingTop();
        int initialPaddingRight = mainView.getPaddingRight();
        int initialPaddingBottom = mainView.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    initialPaddingLeft + systemBars.left,
                    initialPaddingTop + systemBars.top,
                    initialPaddingRight + systemBars.right,
                    initialPaddingBottom + systemBars.bottom
            );
            return insets;
        });
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
        pendingListPosition = savedInstanceState.getInt(STATE_LIST_POSITION, RecyclerView.NO_POSITION);
        favoritesOnly = savedInstanceState.getBoolean(STATE_FAVORITES_ONLY, false);
        selectedCategoryFilter = savedInstanceState.getString(STATE_CATEGORY_FILTER, "Todas");
        dashboardExpanded = savedInstanceState.getBoolean(STATE_DASHBOARD_EXPANDED, false);
    }

    private void loadTrips() {
        allTrips.clear();
        allTrips.addAll(databaseHelper.getAllTrips());
        updateTripStats();
        applyFilters();
    }

    private void updateTripStats() {
        if (statsTotalText == null || statsAverageText == null || statsFavoriteText == null) {
            return;
        }
        statsTotalText.setText(String.valueOf(allTrips.size()));
        int favoriteTotal = 0;
        for (Trip trip : allTrips) {
            if (trip.isFavorite()) {
                favoriteTotal++;
            }
        }
        statsFavoriteText.setText(String.valueOf(favoriteTotal));
        TripStats stats = TripStatsCalculator.fromTrips(allTrips);
        setTextIfPresent(statsBudgetText, MoneyUtils.format(stats.getTotalBudget()));
        setTextIfPresent(statsBestText, stats.getBestTrip() == null ? "-" : stats.getBestTrip().getPlaceName());
        setTextIfPresent(statsLatestText, stats.getLatestTrip() == null ? "-" : stats.getLatestTrip().getPlaceName());
        setTextIfPresent(statsCategoryText, stats.getMostCommonCategory());
        if (allTrips.isEmpty()) {
            statsAverageText.setText("0.0★");
            return;
        }

        int ratingTotal = 0;
        for (Trip trip : allTrips) {
            ratingTotal += trip.getRating();
        }
        float average = (float) ratingTotal / allTrips.size();
        statsAverageText.setText(String.format(Locale.getDefault(), "%.1f★", average));
    }

    private void setTextIfPresent(TextView textView, String value) {
        if (textView != null) {
            textView.setText(value);
        }
    }

    private void setupDashboardControls() {
        View dashboardHeader = findViewById(R.id.dashboardExtraHeader);
        if (dashboardHeader == null || dashboardExtraPanel == null) {
            return;
        }
        updateDashboardVisibility();
        dashboardHeader.setOnClickListener(v -> {
            dashboardExpanded = !dashboardExpanded;
            updateDashboardVisibility();
        });
    }

    private void updateDashboardVisibility() {
        if (dashboardExtraPanel == null) {
            return;
        }
        dashboardExtraPanel.setVisibility(dashboardExpanded ? View.VISIBLE : View.GONE);
        if (dashboardToggleText != null) {
            dashboardToggleText.setText(dashboardExpanded ? "⌃" : "⌄");
        }
    }

    private void setupFilterControls() {
        View filterHeader = findViewById(R.id.filterHeader);
        filterBody = findViewById(R.id.filterBody);
        filterToggleText = findViewById(R.id.filterToggleText);
        searchEditText = findViewById(R.id.searchEditText);
        favoriteFilterCheckBox = findViewById(R.id.favoriteFilterCheckBox);
        categoryFilterSpinner = findViewById(R.id.categoryFilterSpinner);
        ratingFilterSpinner = findViewById(R.id.ratingFilterSpinner);
        sortBySpinner = findViewById(R.id.sortBySpinner);
        sortDirectionSpinner = findViewById(R.id.sortDirectionSpinner);
        Button clearFiltersButton = findViewById(R.id.clearFiltersButton);

        setupSpinner(ratingFilterSpinner, new String[]{"Todas", "5★", "4★", "3★", "2★", "1★"});
        setupSpinner(categoryFilterSpinner, new String[]{"Todas", "Ciudad", "Playa", "Montaña", "Comida", "Cultura", "Naturaleza", "Otro"});
        setupSpinner(sortBySpinner, new String[]{"Actualización", "Lugar", "Calificación", "Fecha"});
        setupSpinner(sortDirectionSpinner, new String[]{"Descendente", "Ascendente"});

        searchEditText.setText(searchQuery);
        favoriteFilterCheckBox.setChecked(favoritesOnly);
        categoryFilterSpinner.setSelection(categoryToFilterPosition(selectedCategoryFilter));
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

        favoriteFilterCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            favoritesOnly = isChecked;
            applyFilters();
        });

        categoryFilterSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategoryFilter = parent.getItemAtPosition(position).toString();
                applyFilters();
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
            favoriteFilterCheckBox.setChecked(false);
            categoryFilterSpinner.setSelection(0);
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
            boolean matchesFavorite = !favoritesOnly || trip.isFavorite();
            boolean matchesCategory = "Todas".equals(selectedCategoryFilter) || selectedCategoryFilter.equals(trip.getCategory());
            if (matchesSearch && matchesRating && matchesFavorite && matchesCategory) {
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
        restoreListPositionIfNeeded(filteredTrips.size());
    }

    private Comparator<Trip> getTripComparator() {
        switch (selectedSortBy) {
            case SORT_PLACE:
                return Comparator.comparing(trip -> safeText(trip.getPlaceName()), String.CASE_INSENSITIVE_ORDER);
            case SORT_RATING:
                return Comparator.comparingInt(Trip::getRating);
            case SORT_DATE:
                return Comparator.comparingLong(trip -> TripDateUtils.toSortableMillis(trip.getTripDate()));
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

    private int categoryToFilterPosition(String category) {
        String[] categories = {"Todas", "Ciudad", "Playa", "Montaña", "Comida", "Cultura", "Naturaleza", "Otro"};
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) {
                return i;
            }
        }
        return 0;
    }

    private void openNewTrip() {
        startActivity(new Intent(this, DetailActivity.class));
    }

    private void openTrip(Trip trip) {
        selectedTripId = trip.getId();
        showTabletTrip(trip);
        if (tabletDetailPanel != null) {
            return;
        }
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EXTRA_TRIP_ID, trip.getId());
        startActivity(intent);
    }

    private void restoreListPositionIfNeeded(int itemCount) {
        if (pendingListPosition == RecyclerView.NO_POSITION || itemCount == 0) {
            return;
        }
        int position = Math.min(pendingListPosition, itemCount - 1);
        pendingListPosition = RecyclerView.NO_POSITION;
        tripsLayoutManager.scrollToPosition(position);
    }

    private void bindTabletViews() {
        tabletDetailPanel = findViewById(R.id.tabletDetailPanel);
        tabletEmptyDetailText = findViewById(R.id.tabletEmptyDetailText);
        tabletPlaceText = findViewById(R.id.tabletPlaceText);
        tabletDateText = findViewById(R.id.tabletDateText);
        tabletCategoryText = findViewById(R.id.tabletCategoryText);
        tabletFavoriteText = findViewById(R.id.tabletFavoriteText);
        tabletCompanionsText = findViewById(R.id.tabletCompanionsText);
        tabletBudgetText = findViewById(R.id.tabletBudgetText);
        tabletDescriptionText = findViewById(R.id.tabletDescriptionText);
        tabletRatingText = findViewById(R.id.tabletRatingText);
        tabletImageView = findViewById(R.id.tabletImageView);
        tabletGalleryRow = findViewById(R.id.tabletGalleryRow);
        tabletGalleryImageOne = findViewById(R.id.tabletGalleryImageOne);
        tabletGalleryImageTwo = findViewById(R.id.tabletGalleryImageTwo);
        tabletGalleryImageThree = findViewById(R.id.tabletGalleryImageThree);
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
        tabletCategoryText.setText(trip.getCategory());
        tabletCategoryText.setBackgroundResource(categoryBackground(trip.getCategory()));
        tabletFavoriteText.setVisibility(trip.isFavorite() ? View.VISIBLE : View.GONE);
        setTextIfPresent(tabletCompanionsText, "Viajé con: " + safeOptional(trip.getCompanions()));
        setTextIfPresent(tabletBudgetText, "Gasto: " + MoneyUtils.format(trip.getBudget()));
        tabletDescriptionText.setText(trip.getDescription());
        tabletRatingText.setText("★ " + trip.getRating() + "/5");
        showImageOn(tabletImageView, trip.getImageUri());
        showTabletGallery(databaseHelper.getTripImages(trip.getId()));
        tabletOpenButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(EXTRA_TRIP_ID, trip.getId());
            startActivity(intent);
        });
    }

    private void shareTrip(Trip trip) {
        ArrayList<Uri> shareImageUris = getShareImageUris(trip);
        Intent sendIntent = new Intent(shareImageUris.size() > 1 ? Intent.ACTION_SEND_MULTIPLE : Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, TripShareUtils.buildShareText(trip));
        if (shareImageUris.size() > 1) {
            sendIntent.setType("image/*");
            sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, shareImageUris);
            addReadPermissions(sendIntent, shareImageUris);
        } else if (shareImageUris.size() == 1) {
            sendIntent.setType("image/*");
            sendIntent.putExtra(Intent.EXTRA_STREAM, shareImageUris.get(0));
            addReadPermissions(sendIntent, shareImageUris);
        } else {
            sendIntent.setType("text/plain");
        }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_trip)));
    }

    private ArrayList<Uri> getShareImageUris(Trip trip) {
        ArrayList<Uri> imageUris = new ArrayList<>();
        addShareImageUri(imageUris, trip.getImageUri());
        for (String extraImageUri : databaseHelper.getTripImages(trip.getId())) {
            addShareImageUri(imageUris, extraImageUri);
        }
        return imageUris;
    }

    private void addShareImageUri(ArrayList<Uri> imageUris, String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        Uri uri = Uri.parse(value);
        for (Uri existingUri : imageUris) {
            if (existingUri.equals(uri)) {
                return;
            }
        }
        imageUris.add(uri);
    }

    private void addReadPermissions(Intent intent, ArrayList<Uri> imageUris) {
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ClipData clipData = null;
        for (Uri uri : imageUris) {
            if (clipData == null) {
                clipData = ClipData.newUri(getContentResolver(), getString(R.string.app_name), uri);
            } else {
                clipData.addItem(new ClipData.Item(uri));
            }
        }
        if (clipData != null) {
            intent.setClipData(clipData);
        }
    }

    private void showTabletGallery(List<String> imageUris) {
        if (tabletGalleryRow == null) {
            return;
        }
        boolean hasExtraImages = imageUris != null && !imageUris.isEmpty();
        tabletGalleryRow.setVisibility(hasExtraImages ? View.VISIBLE : View.GONE);
        if (!hasExtraImages) {
            return;
        }
        bindTabletGalleryImage(tabletGalleryImageOne, imageUris, 0);
        bindTabletGalleryImage(tabletGalleryImageTwo, imageUris, 1);
        bindTabletGalleryImage(tabletGalleryImageThree, imageUris, 2);
    }

    private void bindTabletGalleryImage(ImageView imageView, List<String> imageUris, int index) {
        if (imageView == null) {
            return;
        }
        if (index >= imageUris.size()) {
            imageView.setVisibility(View.GONE);
            imageView.setOnClickListener(null);
            return;
        }
        String imageUri = imageUris.get(index);
        imageView.setVisibility(View.VISIBLE);
        showImageOn(imageView, imageUri);
        imageView.setOnClickListener(v -> showImageOn(tabletImageView, imageUri));
    }

    private void showImageOn(ImageView imageView, String uri) {
        if (imageView == null) {
            return;
        }
        if (!TextUtils.isEmpty(uri)) {
            try {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageURI(Uri.parse(uri));
            } catch (RuntimeException ignored) {
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setImageResource(R.drawable.ic_image_placeholder);
            }
        } else {
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageResource(R.drawable.ic_image_placeholder);
        }
    }

    private String safeOptional(String value) {
        return TextUtils.isEmpty(value) ? "No especificado" : value;
    }

    public static int categoryBackground(String category) {
        if ("Playa".equals(category)) {
            return R.drawable.bg_card_sky;
        }
        if ("Montaña".equals(category) || "Naturaleza".equals(category)) {
            return R.drawable.bg_card_mint;
        }
        if ("Comida".equals(category)) {
            return R.drawable.bg_card_peach;
        }
        if ("Cultura".equals(category) || "Ciudad".equals(category)) {
            return R.drawable.bg_card_lavender;
        }
        return R.drawable.bg_filter_panel;
    }

    private abstract static class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
