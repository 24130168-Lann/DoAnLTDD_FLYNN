
package com.example.doanltdd_flynn.repository;

import androidx.annotation.NonNull;

import com.example.doanltdd_flynn.model.MyPlant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyPlantRepository {

    private FirebaseFirestore db;
    private CollectionReference userCollection;

    public MyPlantRepository() {
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("nguoi_dung");
    }

    // Thêm cây cá nhân
    public void insert(MyPlant myPlant,
                       OnSuccessListener<Void> successListener,
                       OnFailureListener failureListener) {

        String userId = myPlant.getUserId();

        String plantId = userCollection
                .document(userId)
                .collection("cay_ca_nhan")
                .document()
                .getId();

        myPlant.setMyPlantId(plantId);

        userCollection.document(userId)
                .collection("cay_ca_nhan")
                .document(plantId)
                .set(myPlant)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    // Lấy danh sách cây của người dùng
    public void getByUserId(String userId,
                            OnSuccessListener<List<MyPlant>> successListener,
                            OnFailureListener failureListener) {

        userCollection.document(userId)
                .collection("cay_ca_nhan")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List<MyPlant> plantList = new ArrayList<>();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        MyPlant plant = document.toObject(MyPlant.class);

                        if (plant != null) {
                            plant.setMyPlantId(document.getId());
                            plantList.add(plant);
                        }
                    }

                    successListener.onSuccess(plantList);
                })
                .addOnFailureListener(failureListener);
    }

    // Tìm một cây theo ID
    public void findById(String userId, String myPlantId,
                         OnSuccessListener<MyPlant> successListener,
                         OnFailureListener failureListener) {

        userCollection.document(userId)
                .collection("cay_ca_nhan")
                .document(myPlantId)
                .get()
                .addOnSuccessListener(document -> {

                    MyPlant plant = document.toObject(MyPlant.class);

                    if (plant != null) {
                        plant.setMyPlantId(document.getId());
                    }

                    successListener.onSuccess(plant);
                })
                .addOnFailureListener(failureListener);
    }

    // Cập nhật thông tin cây
    public void update(String userId, MyPlant myPlant,
                       OnSuccessListener<Void> successListener,
                       OnFailureListener failureListener) {

        userCollection.document(userId)
                .collection("cay_ca_nhan")
                .document(myPlant.getMyPlantId())
                .set(myPlant)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    // Xóa cây cá nhân
    public void delete(String userId, String myPlantId,
                       OnSuccessListener<Void> successListener,
                       OnFailureListener failureListener) {

        userCollection.document(userId)
                .collection("cay_ca_nhan")
                .document(myPlantId)
                .delete()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
}
