package com.example.doanltdd_flynn.model;

public class CareLog {

    private String careLogId;
    private String myPlantId;
    private CareType careType;
    private String careDate;
    private String note;

    // Constructor rỗng cho Firebase Firestore
    public CareLog() {
    }

    // Constructor đầy đủ
    public CareLog(String careLogId, String myPlantId,
                   CareType careType, String careDate, String note) {

        this.careLogId = careLogId;
        this.myPlantId = myPlantId;
        this.careType = careType;
        this.careDate = careDate;
        this.note = note;
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

    public CareType getCareType() {
        return careType;
    }

    public void setCareType(CareType careType) {
        this.careType = careType;
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
}

