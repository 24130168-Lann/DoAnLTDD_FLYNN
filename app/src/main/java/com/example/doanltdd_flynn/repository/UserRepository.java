package com.example.doanltdd_flynn.repository;

import com.example.doanltdd_flynn.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * THÊM MỚI: đọc kỷ lục chuỗi của tài khoản. Tài liệu chưa có hoặc chưa có trường
     * ky_luc_chuoi thì trả về 0 (đúng "mặc định 0" ở mục 8 bản v4), không báo lỗi.
     */
    public void getMaxStreakRecord(String userId,
                                   OnSuccessListener<Integer> successListener,
                                   OnFailureListener failureListener) {

        db.collection("nguoi_dung")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    Long value = document.exists() ? document.getLong("ky_luc_chuoi") : null;
                    successListener.onSuccess(value == null ? 0 : value.intValue());
                })
                .addOnFailureListener(failureListener);
    }

   
    public void updateMaxStreakRecord(String userId,
                                      int maxStreakRecord,
                                      OnSuccessListener<Void> successListener,
                                      OnFailureListener failureListener) {

        Map<String, Object> data = new HashMap<>();
        data.put("ky_luc_chuoi", maxStreakRecord);

        db.collection("nguoi_dung")
                .document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
}
