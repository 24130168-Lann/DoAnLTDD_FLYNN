package com.example.doanltdd_flynn.utils;

import com.example.doanltdd_flynn.model.MyPlant;
import com.example.doanltdd_flynn.model.StreakState;
import com.example.doanltdd_flynn.model.StreakSummary;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;


public final class ChuoiUtil {

    public static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ChuoiUtil() {
    }

    

    public static LocalDate todayDate() {
        return LocalDate.now(ZONE);
    }

    /** Hôm nay dạng yyyy-MM-dd. */
    public static String today() {
        return todayDate().toString();
    }

    /** yyyy-MM-dd -> LocalDate; rỗng hoặc sai định dạng trả về null. */
    public static LocalDate toDate(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /** LocalDate -> yyyy-MM-dd (dạng lưu Firestore). */
    public static String toKey(LocalDate d) {
        return d.toString();
    }

    /** yyyy-MM-dd -> dd/MM/yyyy (dạng hiển thị trên màn hình). */
    public static String display(String key) {
        LocalDate d = toDate(key);
        return d == null ? "" : d.format(DISPLAY);
    }


    private static TreeSet<LocalDate> toDateSet(List<String> careDays) {
        TreeSet<LocalDate> set = new TreeSet<>();
        if (careDays != null) {
            for (String s : careDays) {
                LocalDate d = toDate(s);
                if (d != null) set.add(d); // dùng tập nên không bao giờ trùng ngày
            }
        }
        return set;
    }

    
    public static int calculateCurrentStreak(List<String> careDays) {
        return calculateCurrentStreak(careDays, todayDate());
    }

    public static int calculateCurrentStreak(List<String> careDays, LocalDate today) {
        TreeSet<LocalDate> set = toDateSet(careDays);
        LocalDate cursor;
        if (set.contains(today)) {
            cursor = today;
        } else if (set.contains(today.minusDays(1))) {
            cursor = today.minusDays(1);
        } else {
            return 0;
        }
        int count = 0;
        while (set.contains(cursor)) {
            count++;
            cursor = cursor.minusDays(1);
        }
        return count;
    }

    public static int calculateLongestStreak(List<String> careDays) {
        TreeSet<LocalDate> set = toDateSet(careDays);
        int best = 0;
        int run = 0;
        LocalDate prev = null;
        for (LocalDate d : set) {
            if (prev != null && prev.plusDays(1).equals(d)) {
                run++;
            } else {
                run = 1;
            }
            if (run > best) best = run;
            prev = d;
        }
        return best;
    }

    /** Tổng số ngày đã chăm (không tính trùng). */
    public static int countCareDays(List<String> careDays) {
        return toDateSet(careDays).size();
    }

    /** Trạng thái chuỗi hiển thị: Đã chăm hôm nay / Chờ chăm hôm nay / Chưa có chuỗi. */
    public static StreakState getStreakState(List<String> careDays) {
        return getStreakState(careDays, todayDate());
    }

    public static StreakState getStreakState(List<String> careDays, LocalDate today) {
        TreeSet<LocalDate> set = toDateSet(careDays);
        if (set.contains(today)) return StreakState.CARED_TODAY;
        if (set.contains(today.minusDays(1))) return StreakState.WAITING;
        return StreakState.NONE;
    }

   
    public static int calculateCompanionDays(String plantingDate, String endDate) {
        LocalDate start = toDate(plantingDate);
        LocalDate end = toDate(endDate);
        if (start == null || end == null) return 0;
        long days = ChronoUnit.DAYS.between(start, end) + 1;
        return (int) Math.max(days, 1);
    }

    public static int calculateCompanionDays(String plantingDate) {
        return calculateCompanionDays(plantingDate, today());
    }

  

    public static StreakSummary summarize(List<MyPlant> plants, int maxStreakRecord) {
        return summarize(plants, maxStreakRecord, todayDate());
    }

    public static StreakSummary summarize(List<MyPlant> plants, int maxStreakRecord, LocalDate today) {
        int highest = 0;
        int holding = 0;
        int total = plants == null ? 0 : plants.size();
        if (plants != null) {
            for (MyPlant p : plants) {
                int cur = calculateCurrentStreak(p.getCareDays(), today);
                if (cur > highest) highest = cur;
                if (cur > 0) holding++; // kể cả trạng thái chờ chăm
            }
        }
        return new StreakSummary(highest, holding, total, maxStreakRecord);
    }

  
    public static List<MyPlant> sortForDisplay(List<MyPlant> plants) {
        return sortForDisplay(plants, todayDate());
    }

    public static List<MyPlant> sortForDisplay(List<MyPlant> plants, LocalDate today) {
        List<MyPlant> sorted = new ArrayList<>(plants);
        Collections.sort(sorted, (a, b) -> {
            int ra = rank(getStreakState(a.getCareDays(), today));
            int rb = rank(getStreakState(b.getCareDays(), today));
            if (ra != rb) return Integer.compare(ra, rb);
            String na = a.getPlantName() == null ? "" : a.getPlantName();
            String nb = b.getPlantName() == null ? "" : b.getPlantName();
            return na.compareToIgnoreCase(nb);
        });
        return sorted;
    }

    private static int rank(StreakState s) {
        switch (s) {
            case WAITING:
                return 0;
            case NONE:
                return 1;
            default:
                return 2;
        }
    }

    /** Kỷ lục mới sau khi chăm: không bao giờ giảm (A15, A19). */
    public static int newRecord(int oldRecord, int currentStreak) {
        return Math.max(oldRecord, currentStreak);
    }
}
