package com.example.doanltdd_flynn.model;

/** Một dòng đề xuất khi thêm cây (lấy từ Bách khoa hoặc danh sách mặc định). */
public class PlantSuggestion {
    private final String plantId; // null nếu là đề xuất mặc định (chưa có trong Bách khoa)
    private final String name;

    public PlantSuggestion(String plantId, String name) {
        this.plantId = plantId;
        this.name = name;
    }

    public String getPlantId() { return plantId; }
    public String getName() { return name; }
}
