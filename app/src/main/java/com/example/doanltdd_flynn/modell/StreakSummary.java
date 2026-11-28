package com.example.doanltdd_flynn.model;


public class StreakSummary {
    private final int highestCurrentStreak; // chuỗi hiện tại cao nhất trong các cây
    private final int holdingCount;         // số cây đang giữ chuỗi (chuỗi > 0)
    private final int totalPlants;          // tổng số cây
    private final int maxStreakRecord;      // kỷ lục chuỗi của tài khoản (lưu cố định)

    public StreakSummary(int highestCurrentStreak, int holdingCount,
                         int totalPlants, int maxStreakRecord) {
        this.highestCurrentStreak = highestCurrentStreak;
        this.holdingCount = holdingCount;
        this.totalPlants = totalPlants;
        this.maxStreakRecord = maxStreakRecord;
    }

    public int getHighestCurrentStreak() { return highestCurrentStreak; }
    public int getHoldingCount() { return holdingCount; }
    public int getTotalPlants() { return totalPlants; }
    public int getMaxStreakRecord() { return maxStreakRecord; }
}
