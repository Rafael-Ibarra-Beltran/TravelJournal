package com.example.traveljournal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_TRIP_ID = "com.example.traveljournal.EXTRA_TRIP_ID";

    private TripDatabaseHelper databaseHelper;
    private TripAdapter tripAdapter;
    private LinearLayout emptyStateLayout;
    private View tabletDetailPanel;
    private TextView tabletEmptyDetailText;
    private TextView tabletPlaceText;
    private TextView tabletDateText;
    private TextView tabletDescriptionText;
    private TextView tabletRatingText;
    private ImageView tabletImageView;
    private Button tabletOpenButton;
    private long selectedTripId = -1L;

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
        RecyclerView tripsRecyclerView = findViewById(R.id.tripsRecyclerView);
        tripsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tripAdapter = new TripAdapter(this::openTrip);
        tripsRecyclerView.setAdapter(tripAdapter);

        findViewById(R.id.addTripButton).setOnClickListener(v -> openNewTrip());
        bindTabletViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrips();
    }

    private void loadTrips() {
        List<Trip> trips = databaseHelper.getAllTrips();
        tripAdapter.submitList(trips);
        emptyStateLayout.setVisibility(trips.isEmpty() ? View.VISIBLE : View.GONE);
        updateTabletSelection(trips);
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
}
