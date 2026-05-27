package com.example.traveljournal.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public final class TripDateUtils {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String LEGACY_DATE_FORMAT = "yyyy-MM-dd";

    private TripDateUtils() {
    }

    public static long toSortableMillis(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Long.MIN_VALUE;
        }
        try {
            return parse(DATE_FORMAT, value).getTime();
        } catch (ParseException ignored) {
            try {
                return parse(LEGACY_DATE_FORMAT, value).getTime();
            } catch (ParseException ex) {
                return Long.MIN_VALUE;
            }
        }
    }

    private static java.util.Date parse(String pattern, String value) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
        formatter.setLenient(false);
        return formatter.parse(value.trim());
    }
}
