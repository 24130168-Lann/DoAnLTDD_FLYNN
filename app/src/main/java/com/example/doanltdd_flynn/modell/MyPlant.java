package com.example.doanltdd_flynn.model;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.PropertyName;

public class MyPlant {

    private String myPlantId;
    private String userId;
    private String plantId;
    private String plantName;
    private String nickname;
    private String plantingDate;
    private String imageUrl;
    private String note;
    private String status;

    private List<String> careDays;

    // Constructor rỗng cho Firebase Firestore
    public MyPlant() {
        careDays = new ArrayList<>();
        status = "DANG_TRONG";
    }

    // Constructor đầy đủ
    public MyPlant(String myPlantId, String userId, String plantId,
                   String plantName, String nickname,
                   String plantingDate, String imageUrl,
                   String note, List<String> careDays) {

        this.myPlantId = myPlantId;
        this.userId = userId;
        this.plantId = plantId;
        this.plantName = plantName;
        this.nickname = nickname;
        this.plantingDate = plantingDate;
        this.imageUrl = imageUrl;
        this.note = note;
        this.status = "DANG_TRONG";

        if (careDays != null) {
            this.careDays = careDays;
        } else {
            this.careDays = new ArrayList<>();
        }
    }

    public String getMyPlantId() {
        return myPlantId;
    }

    public void setMyPlantId(String myPlantId) {
        this.myPlantId = myPlantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPlantingDate() {
        return plantingDate;
    }

    public void setPlantingDate(String plantingDate) {
        this.plantingDate = plantingDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @PropertyName("ngay_cham")
    public List<String> getCareDays() {
        return careDays;
    }

    @PropertyName("ngay_cham")
    public void setCareDays(List<String> careDays) {
        if (careDays != null) {
            this.careDays = careDays;
        } else {
            this.careDays = new ArrayList<>();
        }
    }
}
