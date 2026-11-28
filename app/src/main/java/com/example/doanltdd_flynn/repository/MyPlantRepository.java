package com.example.doanltdd_flynn.repository;

import com.example.doanltdd_flynn.model.MyPlant;
import com.example.doanltdd_flynn.model.PlantHistory;
import com.example.doanltdd_flynn.utils.TaskHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class MyPlantRepository {

    private static final int BATCH_LIMIT = 400; // Firestore giới hạn 500 thao tác mỗi batch

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

        TaskHelper.callbackOnce(
                userCollection.document(userId)
                        .collection("cay_ca_nhan")
                        .document(plantId)
                        .set(myPlant),
                successListener, failureListener);
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

    // Cập nhật thông tin cây.
    // CẢNH BÁO: set() ghi đè cả ngay_cham. Chỉ dùng để sửa tên / ghi chú với MyPlant
    // vừa đọc xong; KHÔNG dùng để thêm ngày chăm (hãy dùng CareLogRepository.insertWithCare).
    public void update(String userId, MyPlant myPlant,
                       OnSuccessListener<Void> successListener,
                       OnFailureListener failureListener) {

        TaskHelper.callbackOnce(
                userCollection.document(userId)
                        .collection("cay_ca_nhan")
                        .document(myPlant.getMyPlantId())
                        .set(myPlant),
                successListener, failureListener);
    }

    // Xóa cây cá nhân (chỉ xóa tài liệu cây, giữ lại từ code của bạn)
    public void delete(String userId, String myPlantId,
                       OnSuccessListener<Void> successListener,
                       OnFailureListener failureListener) {

        TaskHelper.callbackOnce(
                userCollection.document(userId)
                        .collection("cay_ca_nhan")
                        .document(myPlantId)
                        .delete(),
                successListener, failureListener);
    }

    /**
     * THÊM MỚI (A15, mục 5.4 bản v4): xóa cây trong MỘT batch gồm
     *  - ghi thẻ kỷ niệm vào lich_su_cay
     *  - xóa toàn bộ nhật ký con (nhat_ky)
     *  - xóa tài liệu cây gốc
     * Không đụng tới ky_luc_chuoi của tài khoản nên kỷ lục được giữ nguyên.
     */
    public void deleteWithHistory(String userId, String myPlantId, PlantHistory history,
                                  OnSuccessListener<Void> successListener,
                                  OnFailureListener failureListener) {

        DocumentReference userRef = userCollection.document(userId);
        DocumentReference plantRef = userRef.collection("cay_ca_nhan").document(myPlantId);

        plantRef.collection("nhat_ky").get()
                .addOnSuccessListener(logs -> {

                    List<DocumentReference> logRefs = new ArrayList<>();
                    for (DocumentSnapshot d : logs.getDocuments()) {
                        logRefs.add(d.getReference());
                    }

                    DocumentReference historyRef = userRef.collection("lich_su_cay").document();
                    history.setHistoryId(historyRef.getId());

                    int firstChunk = Math.min(BATCH_LIMIT, logRefs.size());
                    List<Task<Void>> commits = new ArrayList<>();

                    // Batch chính: thẻ kỷ niệm + xóa cây gốc + (tối đa 400) nhật ký đầu
                    WriteBatch main = db.batch();
                    main.set(historyRef, history);
                    main.delete(plantRef);
                    for (int i = 0; i < firstChunk; i++) {
                        main.delete(logRefs.get(i));
                    }
                    commits.add(main.commit());

                    // Nhật ký còn lại (rất hiếm) xóa thêm theo từng nhóm
                    for (int i = firstChunk; i < logRefs.size(); i += BATCH_LIMIT) {
                        WriteBatch extra = db.batch();
                        int end = Math.min(i + BATCH_LIMIT, logRefs.size());
                        for (int j = i; j < end; j++) {
                            extra.delete(logRefs.get(j));
                        }
                        commits.add(extra.commit());
                    }

                    TaskHelper.callbackOnce(Tasks.whenAll(commits), successListener, failureListener);
                })
                .addOnFailureListener(failureListener);
    }
}
