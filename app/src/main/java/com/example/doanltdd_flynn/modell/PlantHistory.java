package com.example.doanltdd_flynn.model;

import com.google.firebase.firestore.Exclude;

public class PlantHistory {

    private String historyId;
    private String userId;
    private String plantName;
    private String imageUrl;
    private int companionDays;
    private int longestStreak;
    private String deletedAt;

    // Constructor rỗng cho Firebase Firestore
    public PlantHistory() {
    }

    // Constructor đầy đủ
    public PlantHistory(String historyId, String userId,
                        String plantName, String imageUrl,
                        int companionDays, int longestStreak,
                        String deletedAt) {

        this.historyId = historyId;
        this.userId = userId;
        this.plantName = plantName;
        this.imageUrl = imageUrl;
        this.companionDays = companionDays;
        this.longestStreak = longestStreak;
        this.deletedAt = deletedAt;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCompanionDays() {
        return companionDays;
    }

    public void setCompanionDays(int companionDays) {
        this.companionDays = companionDays;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    // Tạo nội dung kỷ niệm khi cây bị xóa
    // SỬA: thêm @Exclude, nếu không Firestore sẽ ghi thêm trường "memoryMessage" vào mỗi thẻ kỷ niệm.
    @Exclude
    public String getMemoryMessage() {
        return "Bạn đã ở bên " + plantName
                + " được " + companionDays + " ngày";
    }
}
