package com.example.doanltdd_flynn.repository;

import com.example.doanltdd_flynn.model.PlantHistory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PlantHistoryRepository {

    private FirebaseFirestore db;

    public PlantHistoryRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Lấy collection lịch sử cây của người dùng
    private CollectionReference getHistoryCollection(String userId) {
        return db.collection("nguoi_dung")
                .document(userId)
                .collection("lich_su_cay");
    }

    // Lưu lịch sử cây
    public void insert(PlantHistory history,
                       OnSuccessListener<Void> successListener,
                       OnFailureListener failureListener) {

        String userId = history.getUserId();

        String historyId = getHistoryCollection(userId)
                .document().getId();

        history.setHistoryId(historyId);

        getHistoryCollection(userId)
                .document(historyId)
                .set(history)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    // Lấy danh sách lịch sử cây của người dùng
    public void getByUserId(String userId,
                            OnSuccessListener<List<PlantHistory>> successListener,
                            OnFailureListener failureListener) {

        getHistoryCollection(userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List<PlantHistory> historyList = new ArrayList<>();

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        PlantHistory history =
                                document.toObject(PlantHistory.class);

                        if (history != null) {
                            history.setHistoryId(document.getId());
                            historyList.add(history);
                        }
                    }

                    successListener.onSuccess(historyList);
                })
                .addOnFailureListener(failureListener);
    }
}
