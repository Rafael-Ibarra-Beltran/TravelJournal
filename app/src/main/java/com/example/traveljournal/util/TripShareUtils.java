package com.example.traveljournal.util;

import com.example.traveljournal.model.Trip;

public final class TripShareUtils {
    private TripShareUtils() {
    }

    public static String buildShareText(Trip trip) {
        return "Mi recuerdo de viaje: " + safeText(trip.getPlaceName()) + "\n\n"
                + "Fecha: " + safeText(trip.getTripDate()) + "\n"
                + "Categoría: " + safeText(trip.getCategory()) + "\n"
                + "Calificación: " + starsText(trip.getRating()) + "\n"
                + "Favorito: " + (trip.isFavorite() ? "Sí" : "No") + "\n"
                + "Viajé con: " + optionalText(trip.getCompanions()) + "\n"
                + "Gasto realizado: " + MoneyUtils.format(trip.getBudget()) + "\n\n"
                + "Experiencia:\n" + safeText(trip.getDescription())
                + "\n\nGuardado en Travel Journal.";
    }

    private static String starsText(int rating) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            builder.append('★');
        }
        return builder.toString();
    }

    private static String optionalText(String value) {
        return isEmpty(value) ? "No especificado" : value;
    }

    private static String safeText(String value) {
        return value == null ? "" : value;
    }

    private static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }
}
