
package com.example.doanltdd_flynn.repository;

import com.example.doanltdd_flynn.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserRepository {

    private FirebaseFirestore db;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Lấy thông tin người dùng theo ID
    public void findById(String userId,
                         OnSuccessListener<User> successListener,
                         OnFailureListener failureListener) {

        db.collection("nguoi_dung")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        User user = document.toObject(User.class);

                        if (user != null) {
                            user.setUserId(document.getId());
                        }

                        successListener.onSuccess(user);
                    } else {
                        successListener.onSuccess(null);
                    }
                })
                .addOnFailureListener(failureListener);
    }

    // Cập nhật thông tin người dùng
    public void update(User user,
                       OnSuccessListener<Void> successListener,
                       OnFailureListener failureListener) {

        db.collection("nguoi_dung")
                .document(user.getUserId())
                .set(user)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
    // Cập nhật kỷ lục chuỗi chăm sóc dài nhất
    public void updateMaxStreakRecord(String userId,
                                      int maxStreakRecord,
                                      OnSuccessListener<Void> successListener,
                                      OnFailureListener failureListener) {

        db.collection("nguoi_dung")
                .document(userId)
                .update("ky_luc_chuoi", maxStreakRecord)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
}

