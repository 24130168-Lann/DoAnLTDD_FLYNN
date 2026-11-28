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

    // Kiểm tra dữ liệu nhật ký trước khi lưu
    private String validate(CareLog careLog) {

        if (careLog == null
                || careLog.getMyPlantId() == null
                || careLog.getMyPlantId().trim().isEmpty()) {

            return "Không xác định được cây cần ghi nhật ký";
        }

        LocalDate date = ChuoiUtil.toDate(careLog.getCareDate());

        if (date == null) {
            return "Vui lòng chọn ngày ghi nhật ký";
        }

        if (date.isAfter(ChuoiUtil.todayDate())) {
            return "Ngày ghi nhật ký không được ở tương lai";
        }

        // Kiểm tra ghi chú
        if (careLog.getNote() == null
                || careLog.getNote().trim().isEmpty()) {

            return "Vui lòng nhập ghi chú";
        }

        // Kiểm tra danh sách công việc chăm sóc
        List<CareType> careTypes = careLog.getCareTypes();

        if (careTypes == null || careTypes.isEmpty()) {
            return "Vui lòng chọn ít nhất một công việc chăm sóc";
        }

        // Kiểm tra tình trạng cây
        String condition = careLog.getPlantCondition();

        if (condition == null
                || (!CareLog.CONDITION_GOOD.equals(condition)
                && !CareLog.CONDITION_NORMAL.equals(condition)
                && !CareLog.CONDITION_NEEDS_CARE.equals(condition))) {

            return "Vui lòng chọn tình trạng cây";
        }

        return null;
    }

    // Chuẩn hóa dữ liệu trước khi lưu
    private void prepareCareLog(CareLog careLog) {

        careLog.setNote(careLog.getNote().trim());

        List<CareType> careTypes = careLog.getCareTypes();

        // Đồng bộ trường careType cũ với công việc đầu tiên
        careLog.setCareTypes(careTypes);

        // Đảm bảo danh sách ảnh không bị null
        if (careLog.getImageUrls() == null) {
            careLog.setImageUrls(new ArrayList<>());
        }
    }

    // Thêm nhật ký chăm sóc
    public void addCareLog(
            String userId,
            CareLog careLog,
            OnSuccessListener<Void> successListener,
            OnFailureListener failureListener) {

        String error = validate(careLog);

        if (error != null) {
            failureListener.onFailure(
                    new IllegalArgumentException(error)
            );
            return;
        }

        prepareCareLog(careLog);

        final String today = ChuoiUtil.today();

        // Nhật ký ngày quá khứ chỉ được lưu,
        // không làm thay đổi chuỗi chăm sóc
        if (!today.equals(careLog.getCareDate())) {

            careLogRepository.insert(
                    userId,
                    careLog,
                    successListener,
                    failureListener
            );

            return;
        }

        // Nhật ký hôm nay: cập nhật ngày chăm sóc
        myPlantRepository.findById(
                userId,
                careLog.getMyPlantId(),
                plant -> {

                    if (plant == null) {
                        failureListener.onFailure(
                                new Exception("Không tìm thấy cây")
                        );
                        return;
                    }

                    List<String> careDays =
                            new ArrayList<>(plant.getCareDays());

                    // Một ngày chỉ được tính một lần
                    if (!careDays.contains(today)) {
                        careDays.add(today);
                    }

                    final int newStreak =
                            ChuoiUtil.calculateCurrentStreak(careDays);

                    userRepository.getMaxStreakRecord(
                            userId,
                            oldRecord -> {

                                // Chỉ cập nhật kỷ lục nếu chuỗi mới lớn hơn
                                Integer newRecord =
                                        newStreak > oldRecord
                                                ? newStreak
                                                : null;

                                // Lưu nhật ký, ngày chăm và kỷ lục
                                // trong cùng một batch
                                careLogRepository.insertWithCare(
                                        userId,
                                        careLog,
                                        today,
                                        newRecord,
                                        successListener,
                                        failureListener
                                );

                            },
                            failureListener
                    );

                },
                failureListener
        );
    }

    // Lấy danh sách nhật ký chăm sóc của cây
    // Nhật ký mới nhất hiển thị trước
    public void getCareLogs(
            String userId,
            String myPlantId,
            OnSuccessListener<List<CareLog>> successListener,
            OnFailureListener failureListener) {

        careLogRepository.getByMyPlantId(
                userId,
                myPlantId,
                list -> {

                    Collections.sort(list, (a, b) -> {

                        String dateA = a.getCareDate() == null
                                ? ""
                                : a.getCareDate();

                        String dateB = b.getCareDate() == null
                                ? ""
                                : b.getCareDate();

                        return dateB.compareTo(dateA);
                    });

                    successListener.onSuccess(list);
                },
                failureListener
        );
    }

    // Cập nhật nhật ký chăm sóc
    // Không làm thay đổi ngày chăm và chuỗi chăm sóc
    public void updateCareLog(
            String userId,
            CareLog careLog,
            OnSuccessListener<Void> successListener,
            OnFailureListener failureListener) {

        String error = validate(careLog);

        if (error != null) {
            failureListener.onFailure(
                    new IllegalArgumentException(error)
            );
            return;
        }

        if (careLog.getCareLogId() == null
                || careLog.getCareLogId().trim().isEmpty()) {

            failureListener.onFailure(
                    new IllegalArgumentException(
                            "Không xác định được nhật ký cần sửa"
                    )
            );
            return;
        }

        prepareCareLog(careLog);

        careLogRepository.update(
                userId,
                careLog,
                successListener,
                failureListener
        );
    }

    // Xóa một nhật ký chăm sóc
    // Không làm thay đổi ngày chăm và chuỗi chăm sóc
    public void deleteCareLog(
            String userId,
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
