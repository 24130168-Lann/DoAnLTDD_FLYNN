package com.example.doanltdd_flynn.model;

public class PlantSuggestion {

    private final String plantId;
    private final String name;

    public PlantSuggestion(String plantId, String name) {
        this.plantId = plantId;
        this.name = name;
    }

    public String getPlantId() {
        return plantId;
    }

    public String getName() {
        return name;
    }
}
