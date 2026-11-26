package com.example.doanltdd_flynn.utils;

import java.text.Normalizer;
import java.util.Locale;


public final class TextUtil {
    private TextUtil() {
    }

    public static String normalize(String s) {
        if (s == null) return "";
        String lower = s.toLowerCase(Locale.ROOT).trim();
        String decomposed = Normalizer.normalize(lower, Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{M}", "").replace('đ', 'd');
    }
}
