package com.example.doanltdd_flynn.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CareLog {

    private String careLogId;
    private String myPlantId;

    // Trường cũ: giữ lại để tương thích nhật ký đã lưu trên Firestore
    private CareType careType;

    // Danh sách công việc chăm sóc
    private List<CareType> careTypes;

    private String careDate;
    private String note;

    // Tình trạng cây
    private String plantCondition;

    // Danh sách đường dẫn hình ảnh
    private List<String> imageUrls;

    public static final String CONDITION_GOOD = "Tốt";
    public static final String CONDITION_NORMAL = "Bình thường";
    public static final String CONDITION_NEEDS_CARE = "Cần chăm sóc";

    // Constructor rỗng để Firestore đọc dữ liệu
    public CareLog() {
        careTypes = new ArrayList<>();
        imageUrls = new ArrayList<>();
    }

    // Giữ constructor cũ để không ảnh hưởng code hiện tại
    public CareLog(String careLogId, String myPlantId,
                   CareType careType, String careDate, String note) {

        this.careLogId = careLogId;
        this.myPlantId = myPlantId;
        this.careType = careType;
        this.careDate = careDate;
        this.note = note;

        careTypes = new ArrayList<>();

        if (careType != null) {
            careTypes.add(careType);
        }

        imageUrls = new ArrayList<>();
    }

    public String getCareLogId() {
        return careLogId;
    }

    public void setCareLogId(String careLogId) {
        this.careLogId = careLogId;
    }

    public String getMyPlantId() {
        return myPlantId;
    }

    public void setMyPlantId(String myPlantId) {
        this.myPlantId = myPlantId;
    }

    // Lấy công việc chăm sóc đầu tiên để tương thích code cũ
    public CareType getCareType() {
        if (careTypes != null && !careTypes.isEmpty()) {
            return careTypes.get(0);
        }

        return careType;
    }

    public void setCareType(CareType careType) {
        this.careType = careType;
    }

    // Hỗ trợ dữ liệu nhật ký cũ chỉ có một careType
    public List<CareType> getCareTypes() {
        if (careTypes != null && !careTypes.isEmpty()) {
            return careTypes;
        }

        if (careType != null) {
            return new ArrayList<>(
                    Collections.singletonList(careType)
            );
        }

        return new ArrayList<>();
    }

    public void setCareTypes(List<CareType> careTypes) {
        this.careTypes = careTypes == null
                ? new ArrayList<>()
                : new ArrayList<>(careTypes);

        // Đồng bộ trường cũ với công việc đầu tiên
        this.careType = this.careTypes.isEmpty()
                ? null
                : this.careTypes.get(0);
    }

    public String getCareDate() {
        return careDate;
    }

    public void setCareDate(String careDate) {
        this.careDate = careDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPlantCondition() {
        return plantCondition;
    }

    public void setPlantCondition(String plantCondition) {
        this.plantCondition = plantCondition;
    }

    public List<String> getImageUrls() {
        return imageUrls == null
                ? new ArrayList<>()
                : imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls == null
                ? new ArrayList<>()
                : new ArrayList<>(imageUrls);
    }
}
