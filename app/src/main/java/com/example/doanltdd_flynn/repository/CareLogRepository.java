package com.example.doanltdd_flynn.repository;

import com.example.doanltdd_flynn.model.CareLog;
import com.example.doanltdd_flynn.utils.TaskHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Thêm nhật ký chăm sóc (chỉ lưu nhật ký, dùng cho nhật ký ngày quá khứ - A14)
    public void insert(String userId, CareLog careLog,
                       OnSuccessListener<Void> successListener,
                       OnFailureListener failureListener) {

        String logId = getCareLogCollection(
                userId, careLog.getMyPlantId())
                .document().getId();

        careLog.setCareLogId(logId);

        TaskHelper.callbackOnce(
                getCareLogCollection(userId, careLog.getMyPlantId())
                        .document(logId)
                        .set(careLog),
                successListener, failureListener);
    }

    /**
     * THÊM MỚI: lưu nhật ký của NGÀY HÔM NAY trong MỘT batch (bản thiết kế v4, mục 8):
     *  1. ghi nhật ký vào nhat_ky
     *  2. thêm hôm nay vào ngay_cham của cây bằng FieldValue.arrayUnion
     *     (không trùng ngày, an toàn khi offline hoặc dùng 2 máy)
     *  3. nếu kỷ lục tăng thì ghi ky_luc_chuoi vào tài khoản (SetOptions.merge nên
     *     không lỗi khi tài liệu người dùng chưa tồn tại)
     * Cả ba cùng thành công hoặc cùng thất bại, không còn trạng thái ghi dở.
     *
     * @param newRecord kỷ lục mới cần ghi; null nếu kỷ lục không đổi
     */
    public void insertWithCare(String userId, CareLog careLog, String todayKey, Integer newRecord,
                               OnSuccessListener<Void> successListener,
                               OnFailureListener failureListener) {

        DocumentReference userRef = db.collection("nguoi_dung").document(userId);
        DocumentReference plantRef = userRef.collection("cay_ca_nhan").document(careLog.getMyPlantId());
        DocumentReference logRef = plantRef.collection("nhat_ky").document();

        careLog.setCareLogId(logRef.getId());

        WriteBatch batch = db.batch();
        batch.set(logRef, careLog);
        batch.update(plantRef, "ngay_cham", FieldValue.arrayUnion(todayKey));

        if (newRecord != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("ky_luc_chuoi", newRecord);
            batch.set(userRef, userData, SetOptions.merge());
        }

        TaskHelper.callbackOnce(batch.commit(), successListener, failureListener);
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

    // Cập nhật nhật ký (không đụng tới ngay_cham - A14)
    public void update(String userId, CareLog careLog,
                       OnSuccessListener<Void> successListener,
                       OnFailureListener failureListener) {

        TaskHelper.callbackOnce(
                getCareLogCollection(userId, careLog.getMyPlantId())
                        .document(careLog.getCareLogId())
                        .set(careLog),
                successListener, failureListener);
    }

    // Xóa một nhật ký (không đụng tới ngay_cham - A14)
    public void delete(String userId, String myPlantId, String careLogId,
                       OnSuccessListener<Void> successListener,
                       OnFailureListener failureListener) {

        TaskHelper.callbackOnce(
                getCareLogCollection(userId, myPlantId)
                        .document(careLogId)
                        .delete(),
                successListener, failureListener);
    }

    // Xóa tất cả nhật ký của một cây (giữ lại từ code của bạn; xóa cây giờ dùng
    // MyPlantRepository.deleteWithHistory để thực hiện cùng một batch)
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
