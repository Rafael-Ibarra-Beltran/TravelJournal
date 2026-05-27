package com.example.traveljournal.util;

import com.example.traveljournal.model.Trip;

public class TripStats {
    private final int totalTrips;
    private final double totalBudget;
    private final Trip bestTrip;
    private final Trip latestTrip;
    private final String mostCommonCategory;

    public TripStats(int totalTrips, double totalBudget, Trip bestTrip, Trip latestTrip, String mostCommonCategory) {
        this.totalTrips = totalTrips;
        this.totalBudget = totalBudget;
        this.bestTrip = bestTrip;
        this.latestTrip = latestTrip;
        this.mostCommonCategory = mostCommonCategory;
    }

    public int getTotalTrips() {
        return totalTrips;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public Trip getBestTrip() {
        return bestTrip;
    }

    public Trip getLatestTrip() {
        return latestTrip;
    }

    public String getMostCommonCategory() {
        return mostCommonCategory;
    }
}
