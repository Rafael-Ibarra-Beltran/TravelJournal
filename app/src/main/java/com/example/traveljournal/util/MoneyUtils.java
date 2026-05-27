package com.example.traveljournal.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class MoneyUtils {
    private MoneyUtils() {
    }

    public static String format(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return "$ " + formatter.format(Math.max(0, amount));
    }
}
