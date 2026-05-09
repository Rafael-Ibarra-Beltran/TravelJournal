package com.example.traveljournal.model;

public class Trip {
    private long id;
    private String placeName;
    private String tripDate;
    private String description;
    private int rating;
    private String imageUri;
    private String createdAt;
    private String updatedAt;

    public Trip() {
    }

    public Trip(long id, String placeName, String tripDate, String description, int rating,
                String imageUri, String createdAt, String updatedAt) {
        this.id = id;
        this.placeName = placeName;
        this.tripDate = tripDate;
        this.description = description;
        this.rating = rating;
        this.imageUri = imageUri;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
