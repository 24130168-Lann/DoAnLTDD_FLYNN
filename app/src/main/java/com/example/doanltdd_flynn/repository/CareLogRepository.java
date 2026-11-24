
package com.example.doanltdd_flynn.repository;

import com.example.doanltdd_flynn.model.CareLog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class CareLogRepository {

    private FirebaseFirestore db;

    public CareLogRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Lấy đường dẫn đến nhật ký của một cây
    private CollectionReference getCareLogCollection(
            String userId, String myPlantId) {

        return db.collection("nguoi_dung")
                .document(userId)
                .collection("cay_ca_nhan")
                .document(myPlantId)
                .collection("nhat_ky");
    }

    // Thêm nhật ký chăm sóc
    public void insert(String userId, CareLog careLog,
                       OnSuccessListener<Void> successListener,
                       OnFailureListener failureListener) {

        String logId = getCareLogCollection(
                userId, careLog.getMyPlantId())
                .document().getId();

        careLog.setCareLogId(logId);

        getCareLogCollection(userId, careLog.getMyPlantId())
                .document(logId)
                .set(careLog)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    // Lấy danh sách nhật ký của một cây
    public void getByMyPlantId(String userId, String myPlantId,
                               OnSuccessListener<List<CareLog>> successListener,
                               OnFailureListener failureListener) {

        getCareLogCollection(userId, myPlantId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List<CareLog> careLogList = new ArrayList<>();

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        CareLog careLog = document.toObject(CareLog.class);

                        if (careLog != null) {
                            careLog.setCareLogId(document.getId());
                            careLogList.add(careLog);
                        }
                    }

                    successListener.onSuccess(careLogList);
                })
                .addOnFailureListener(failureListener);
    }

    // Cập nhật nhật ký
    public void update(String userId, CareLog careLog,
                       OnSuccessListener<Void> successListener,
                       OnFailureListener failureListener) {

        getCareLogCollection(userId, careLog.getMyPlantId())
                .document(careLog.getCareLogId())
                .set(careLog)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    // Xóa một nhật ký
    public void delete(String userId, String myPlantId, String careLogId,
                       OnSuccessListener<Void> successListener,
                       OnFailureListener failureListener) {

        getCareLogCollection(userId, myPlantId)
                .document(careLogId)
                .delete()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    // Xóa tất cả nhật ký của một cây
    public void deleteAllByMyPlantId(
            String userId, String myPlantId,
            OnSuccessListener<Void> successListener,
            OnFailureListener failureListener) {

        getCareLogCollection(userId, myPlantId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    WriteBatch batch = db.batch();

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        batch.delete(document.getReference());
                    }

                    batch.commit()
                            .addOnSuccessListener(successListener)
                            .addOnFailureListener(failureListener);
                })
                .addOnFailureListener(failureListener);
    }
}

