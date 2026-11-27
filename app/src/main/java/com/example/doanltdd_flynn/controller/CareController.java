package com.example.doanltdd_flynn.controller;

import com.example.doanltdd_flynn.model.CareLog;
import com.example.doanltdd_flynn.model.CareType;
import com.example.doanltdd_flynn.model.MyPlant;
import com.example.doanltdd_flynn.repository.CareLogRepository;
import com.example.doanltdd_flynn.repository.MyPlantRepository;
import com.example.doanltdd_flynn.repository.UserRepository;
import com.example.doanltdd_flynn.utils.ChuoiUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CareController {
    private final CareLogRepository careLogRepository;
    private final MyPlantRepository myPlantRepository;
    private final UserRepository userRepository;

    public CareController() {
        careLogRepository = new CareLogRepository();
        myPlantRepository = new MyPlantRepository();
        userRepository = new UserRepository();
    }

    // Kiểm tra dữ liệu nhật ký (A06). Trả về thông báo lỗi, hoặc null nếu hợp lệ.
    private String validate(CareLog careLog) {
        if (careLog == null || careLog.getMyPlantId() == null) {
            return "Không xác định được cây cần ghi nhật ký";
        }
        LocalDate date = ChuoiUtil.toDate(careLog.getCareDate());
        if (date == null) {
            return "Vui lòng chọn ngày ghi nhật ký";
        }
        if (date.isAfter(ChuoiUtil.todayDate())) {
            return "Ngày ghi nhật ký không được ở tương lai";
        }
        if (careLog.getNote() == null || careLog.getNote().trim().isEmpty()) {
            return "Vui lòng nhập ghi chú";
        }
        return null;
    }
    public void addCareLog(String userId,
                           CareLog careLog,
                           OnSuccessListener<Void> successListener,
                           OnFailureListener failureListener) {

        String error = validate(careLog);
        if (error != null) {
            failureListener.onFailure(new IllegalArgumentException(error));
            return;
        }

        careLog.setNote(careLog.getNote().trim());
        if (careLog.getCareType() == null) {
            careLog.setCareType(CareType.OTHER);
        }

        final String today = ChuoiUtil.today();

        // A14: nhật ký ngày quá khứ chỉ được lưu, không làm thay đổi chuỗi
        if (!today.equals(careLog.getCareDate())) {
            careLogRepository.insert(userId, careLog, successListener, failureListener);
            return;
        }

        // Nhật ký hôm nay = hành động chăm sóc: cần chuỗi mới để cập nhật kỷ lục
        myPlantRepository.findById(userId, careLog.getMyPlantId(), plant -> {

            if (plant == null) {
                failureListener.onFailure(new Exception("Không tìm thấy cây"));
                return;
            }

            List<String> careDays = new ArrayList<>(plant.getCareDays());
            if (!careDays.contains(today)) {
                careDays.add(today);
            }
            final int newStreak = ChuoiUtil.calculateCurrentStreak(careDays);

            userRepository.getMaxStreakRecord(userId, oldRecord -> {

                // Chỉ ghi kỷ lục khi thật sự tăng
                Integer newRecord = newStreak > oldRecord ? newStreak : null;

                careLogRepository.insertWithCare(userId, careLog, today, newRecord,
                        successListener, failureListener);

            }, failureListener);

        }, failureListener);
    }

    // Lấy danh sách nhật ký chăm sóc của cây (mới nhất lên đầu)
    public void getCareLogs(String userId,
                            String myPlantId,
                            OnSuccessListener<List<CareLog>> successListener,
                            OnFailureListener failureListener) {

        careLogRepository.getByMyPlantId(
                userId,
                myPlantId,
                list -> {
                    Collections.sort(list, (a, b) -> {
                        String da = a.getCareDate() == null ? "" : a.getCareDate();
                        String db = b.getCareDate() == null ? "" : b.getCareDate();
                        return db.compareTo(da);
                    });
                    successListener.onSuccess(list);
                },
                failureListener
        );
    }

    // Cập nhật nhật ký chăm sóc (A14: không làm thay đổi ngày chăm)
    public void updateCareLog(String userId,
                              CareLog careLog,
                              OnSuccessListener<Void> successListener,
                              OnFailureListener failureListener) {

        String error = validate(careLog);
        if (error != null || careLog.getCareLogId() == null) {
            failureListener.onFailure(new IllegalArgumentException(
                    error != null ? error : "Không xác định được nhật ký cần sửa"));
            return;
        }
        careLog.setNote(careLog.getNote().trim());

        careLogRepository.update(
                userId,
                careLog,
                successListener,
                failureListener
        );
    }

    // Xóa một nhật ký chăm sóc (A14: không làm thay đổi ngày chăm)
    public void deleteCareLog(String userId,
                              String myPlantId,
                              String careLogId,
                              OnSuccessListener<Void> successListener,
                              OnFailureListener failureListener) {

        careLogRepository.delete(
                userId,
                myPlantId,
                careLogId,
                successListener,
                failureListener
        );
    }
}
