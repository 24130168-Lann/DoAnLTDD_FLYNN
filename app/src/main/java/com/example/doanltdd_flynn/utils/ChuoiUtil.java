
package com.example.doanltdd_flynn.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.TreeSet;

public class ChuoiUtil {

    // Múi giờ Việt Nam
    private static final TimeZone TIME_ZONE =
            TimeZone.getTimeZone("Asia/Ho_Chi_Minh");

    // Chuyển ngày thành chuỗi yyyy-MM-dd
    private static String formatDate(Calendar calendar) {
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        sdf.setTimeZone(TIME_ZONE);

        return sdf.format(calendar.getTime());
    }

    // Lấy ngày hôm nay
    public static String getToday() {
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        return formatDate(calendar);
    }

    // Lấy ngày hôm qua
    private static String getYesterday() {
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return formatDate(calendar);
    }

    // Sắp xếp và loại bỏ ngày chăm sóc bị trùng
    private static TreeSet<String> getUniqueCareDays(List<String> careDays) {
        TreeSet<String> uniqueDays = new TreeSet<>();

        if (careDays != null) {
            for (String day : careDays) {
                if (day != null && day.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    uniqueDays.add(day);
                }
            }
        }

        return uniqueDays;
    }

    // Chuyển chuỗi ngày thành Date
    private static Date parseDate(String date) {
        try {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            sdf.setTimeZone(TIME_ZONE);
            sdf.setLenient(false);

            return sdf.parse(date);

        } catch (Exception e) {
            return null;
        }
    }

    // Tính chuỗi chăm sóc hiện tại
    public static int calculateCurrentStreak(List<String> careDays) {

        TreeSet<String> uniqueDays = getUniqueCareDays(careDays);

        if (uniqueDays.isEmpty()) {
            return 0;
        }

        String lastCareDay = uniqueDays.last();

        // Nếu lần chăm sóc cuối không phải hôm nay hoặc hôm qua
        if (!lastCareDay.equals(getToday())
                && !lastCareDay.equals(getYesterday())) {
            return 0;
        }

        Date parsedDate = parseDate(lastCareDay);

        if (parsedDate == null) {
            return 0;
        }

        int streak = 0;

        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.setTime(parsedDate);

        // Đếm ngược từng ngày liên tiếp
        while (true) {

            String currentDay = formatDate(calendar);

            if (uniqueDays.contains(currentDay)) {
                streak++;
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            } else {
                break;
            }
        }

        return streak;
    }

    // Tính kỷ lục chuỗi chăm sóc dài nhất
    public static int calculateLongestStreak(List<String> careDays) {

        TreeSet<String> uniqueDays = getUniqueCareDays(careDays);

        if (uniqueDays.isEmpty()) {
            return 0;
        }

        int longestStreak = 1;
        int currentStreak = 1;

        String previousDay = null;

        for (String day : uniqueDays) {

            if (previousDay != null) {

                Date parsedDate = parseDate(previousDay);

                if (parsedDate == null) {
                    previousDay = day;
                    currentStreak = 1;
                    continue;
                }

                Calendar calendar = Calendar.getInstance(TIME_ZONE);
                calendar.setTime(parsedDate);

                calendar.add(Calendar.DAY_OF_MONTH, 1);

                String nextDay = formatDate(calendar);

                if (day.equals(nextDay)) {
                    currentStreak++;
                } else {
                    currentStreak = 1;
                }

                if (currentStreak > longestStreak) {
                    longestStreak = currentStreak;
                }
            }

            previousDay = day;
        }

        return longestStreak;
    }

    // Kiểm tra hôm nay đã chăm sóc cây chưa
    public static boolean hasCareToday(List<String> careDays) {
        TreeSet<String> uniqueDays = getUniqueCareDays(careDays);
        return uniqueDays.contains(getToday());
    }

    // Lấy trạng thái chuỗi chăm sóc
    public static String getStreakStatus(List<String> careDays) {

        TreeSet<String> uniqueDays = getUniqueCareDays(careDays);

        if (uniqueDays.isEmpty()) {
            return "Chưa có ngày chăm sóc";
        }

        String lastCareDay = uniqueDays.last();

        if (lastCareDay.equals(getToday())) {
            return "Đang chăm sóc hôm nay";
        }

        if (lastCareDay.equals(getYesterday())) {
            return "Chờ chăm hôm nay";
        }

        return "Chuỗi đã kết thúc";
    }
}
