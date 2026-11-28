package com.example.doanltdd_flynn.repository;

import com.example.doanltdd_flynn.model.PlantSuggestion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PlantSuggestionRepository {

    // Collection Bách khoa trên Firebase
    private static final String COLLECTION = "cay_bach_khoa";

    // Trường chứa tên tiếng Việt của cây
    private static final String FIELD_NAME = "ten_tieng_viet";

    private final FirebaseFirestore db;

    public PlantSuggestionRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Lấy toàn bộ danh sách cây từ Bách khoa Firebase
    public void getSuggestions(
            OnSuccessListener<List<PlantSuggestion>> successListener,
            OnFailureListener failureListener) {

        db.collection(COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List<PlantSuggestion> list = new ArrayList<>();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                        String name = document.getString(FIELD_NAME);

                        if (name != null && !name.trim().isEmpty()) {

                            PlantSuggestion suggestion = new PlantSuggestion(
                                    document.getId(),
                                    name.trim()
                            );

                            list.add(suggestion);
                        }
                    }

                    // Firebase không có cây thì trả về danh sách rỗng
                    successListener.onSuccess(list);
                })
                .addOnFailureListener(failureListener);
    }
}