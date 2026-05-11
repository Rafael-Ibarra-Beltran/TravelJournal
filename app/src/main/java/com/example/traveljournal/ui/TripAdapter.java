package com.example.traveljournal.ui;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveljournal.R;
import com.example.traveljournal.model.Trip;

import java.util.ArrayList;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }

    private final List<Trip> trips = new ArrayList<>();
    private final OnTripClickListener listener;

    public TripAdapter(OnTripClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.placeNameTextView.setText(trip.getPlaceName());
        holder.tripDateTextView.setText(trip.getTripDate());
        holder.descriptionPreviewTextView.setText(shortDescription(trip.getDescription()));
        holder.ratingTextView.setText("★ " + trip.getRating() + "/5");

        if (!TextUtils.isEmpty(trip.getImageUri())) {
            try {
                holder.tripImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.tripImageView.setImageURI(Uri.parse(trip.getImageUri()));
            } catch (RuntimeException ignored) {
                holder.tripImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                holder.tripImageView.setImageResource(R.drawable.ic_image_placeholder);
            }
        } else {
            holder.tripImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.tripImageView.setImageResource(R.drawable.ic_image_placeholder);
        }

        holder.itemView.setOnClickListener(v -> listener.onTripClick(trip));
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public void submitList(List<Trip> newTrips) {
        trips.clear();
        trips.addAll(newTrips);
        notifyDataSetChanged();
    }

    private String shortDescription(String description) {
        if (description == null) {
            return "";
        }
        if (description.length() <= 90) {
            return description;
        }
        return description.substring(0, 87).trim() + "...";
    }

    static class TripViewHolder extends RecyclerView.ViewHolder {
        final ImageView tripImageView;
        final TextView placeNameTextView;
        final TextView tripDateTextView;
        final TextView descriptionPreviewTextView;
        final TextView ratingTextView;

        TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tripImageView = itemView.findViewById(R.id.tripImageView);
            placeNameTextView = itemView.findViewById(R.id.placeNameTextView);
            tripDateTextView = itemView.findViewById(R.id.tripDateTextView);
            descriptionPreviewTextView = itemView.findViewById(R.id.descriptionPreviewTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
        }
    }
}
