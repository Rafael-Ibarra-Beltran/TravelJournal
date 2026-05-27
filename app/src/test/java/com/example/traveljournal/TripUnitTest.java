package com.example.traveljournal;

import com.example.traveljournal.model.Trip;
import com.example.traveljournal.util.MoneyUtils;
import com.example.traveljournal.util.TripShareUtils;
import com.example.traveljournal.util.TripDateUtils;
import com.example.traveljournal.util.TripStats;
import com.example.traveljournal.util.TripStatsCalculator;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TripUnitTest {
    @Test
    public void tripStoresFavoriteFlag() {
        Trip trip = new Trip();

        assertFalse(trip.isFavorite());

        trip.setFavorite(true);

        assertTrue(trip.isFavorite());
    }

    @Test
    public void tripDateSortsByRealDateInsteadOfText() {
        long januarySecond = TripDateUtils.toSortableMillis("02/01/2026");
        long decemberFirst = TripDateUtils.toSortableMillis("01/12/2025");

        assertTrue(januarySecond > decemberFirst);
    }

    @Test
    public void moneyFormatterShowsCurrencyWithTwoDecimals() {
        assertEquals("$ 1,250.50", MoneyUtils.format(1250.5));
    }

    @Test
    public void shareTextIncludesSavedTripDetails() {
        Trip trip = new Trip();
        trip.setPlaceName("Kioto");
        trip.setTripDate("12/05/2026");
        trip.setCategory("Cultura");
        trip.setRating(5);
        trip.setFavorite(true);
        trip.setCompanions("Amigos");
        trip.setBudget(1250.0);
        trip.setDescription("Templos, comida local y calles antiguas.");

        String shareText = TripShareUtils.buildShareText(trip);

        assertTrue(shareText.contains("Mi recuerdo de viaje: Kioto"));
        assertTrue(shareText.contains("Categoría: Cultura"));
        assertTrue(shareText.contains("Calificación: ★★★★★"));
        assertTrue(shareText.contains("Favorito: Sí"));
        assertTrue(shareText.contains("Viajé con: Amigos"));
        assertTrue(shareText.contains("Gasto realizado: $ 1,250.00"));
    }

    @Test
    public void statsCalculatorFindsTotalsAndCommonCategory() {
        Trip beach = new Trip();
        beach.setPlaceName("Cancún");
        beach.setTripDate("10/01/2026");
        beach.setRating(5);
        beach.setCategory("Playa");
        beach.setBudget(1200.0);

        Trip mountain = new Trip();
        mountain.setPlaceName("Nevado");
        mountain.setTripDate("05/01/2026");
        mountain.setRating(4);
        mountain.setCategory("Montaña");
        mountain.setBudget(800.0);

        TripStats stats = TripStatsCalculator.fromTrips(Arrays.asList(beach, mountain));

        assertEquals(2, stats.getTotalTrips());
        assertEquals(2000.0, stats.getTotalBudget(), 0.001);
        assertEquals("Cancún", stats.getBestTrip().getPlaceName());
        assertEquals("Cancún", stats.getLatestTrip().getPlaceName());
        assertEquals("Montaña", stats.getMostCommonCategory());
    }
}
