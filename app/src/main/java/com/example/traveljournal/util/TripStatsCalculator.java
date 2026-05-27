package com.example.traveljournal.util;

import com.example.traveljournal.model.Trip;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class TripStatsCalculator {
    private TripStatsCalculator() {
    }

    public static TripStats fromTrips(List<Trip> trips) {
        double totalBudget = 0;
        Trip bestTrip = null;
        Trip latestTrip = null;
        Map<String, Integer> categoryCounts = new HashMap<>();

        for (Trip trip : trips) {
            totalBudget += trip.getBudget();
            if (bestTrip == null || trip.getRating() > bestTrip.getRating()) {
                bestTrip = trip;
            }
            if (latestTrip == null || TripDateUtils.toSortableMillis(trip.getTripDate())
                    > TripDateUtils.toSortableMillis(latestTrip.getTripDate())) {
                latestTrip = trip;
            }

            String category = safeCategory(trip.getCategory());
            categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
        }

        return new TripStats(trips.size(), totalBudget, bestTrip, latestTrip, mostCommonCategory(categoryCounts));
    }

    private static String safeCategory(String category) {
        return category == null || category.trim().isEmpty() ? "Otro" : category.trim();
    }

    private static String mostCommonCategory(Map<String, Integer> categoryCounts) {
        String bestCategory = "Sin viajes";
        int bestCount = 0;
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            String category = entry.getKey();
            int count = entry.getValue();
            if (count > bestCount || (count == bestCount
                    && category.toLowerCase(Locale.getDefault()).compareTo(bestCategory.toLowerCase(Locale.getDefault())) < 0)) {
                bestCategory = category;
                bestCount = count;
            }
        }
        return bestCategory;
    }
}
