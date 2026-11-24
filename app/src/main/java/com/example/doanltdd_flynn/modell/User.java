
package com.example.doanltdd_flynn.model;
import com.google.firebase.firestore.PropertyName;

public class User {

    private String userId;
    private String fullName;
    private String email;
    private String avatarUrl;
    private String createdAt;
    private String passwordHash;

    // Kỷ lục chuỗi chăm sóc dài nhất của toàn tài khoản
    private int maxStreakRecord;

    // Constructor rỗng cho Firebase Firestore
    public User() {
    }

    // Constructor đầy đủ
    public User(String userId, String fullName, String email,
                String avatarUrl, String createdAt,
                String passwordHash, int maxStreakRecord) {

        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
        this.passwordHash = passwordHash;
        this.maxStreakRecord = maxStreakRecord;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    @PropertyName("ky_luc_chuoi")
    public int getMaxStreakRecord() {
        return maxStreakRecord;
    }
    @PropertyName("ky_luc_chuoi")
    public void setMaxStreakRecord(int maxStreakRecord) {
        this.maxStreakRecord = maxStreakRecord;
    }
    public void updateProfile(String fullName, String avatarUrl) {
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
    }
}

