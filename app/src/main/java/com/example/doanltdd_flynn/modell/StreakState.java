package com.example.doanltdd_flynn.model;

public enum StreakState {
    CARED_TODAY("Đã chăm hôm nay"),
    WAITING("Chờ chăm hôm nay"),
    NONE("Chưa có chuỗi");

    private final String label;

    StreakState(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
