
package com.example.doanltdd_flynn.controller;

import com.example.doanltdd_flynn.model.CareLog;
import com.example.doanltdd_flynn.model.MyPlant;
import com.example.doanltdd_flynn.repository.CareLogRepository;
import com.example.doanltdd_flynn.repository.MyPlantRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.example.doanltdd_flynn.repository.UserRepository;
import com.example.doanltdd_flynn.utils.ChuoiUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CareController {
    private final CareLogRepository careLogRepository;
    private final MyPlantRepository myPlantRepository;
    private final UserRepository userRepository;

   public CareController() {
        careLogRepository = new CareLogRepository();
        myPlantRepository = new MyPlantRepository();
        userRepository = new UserRepository();
    }

    // Lấy ngày hiện tại theo múi giờ Việt Nam
    private String getToday() {
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        return sdf.format(new Date());
    }



    // Thêm nhật ký chăm sóc
    public void addCareLog(String userId,
                           CareLog careLog,
                           OnSuccessListener<Void> successListener,
                           OnFailureListener failureListener) {

        careLogRepository.insert(userId, careLog, unused -> {

            // Chỉ cập nhật chuỗi nếu nhật ký là hôm nay
            if (!getToday().equals(careLog.getCareDate())) {
                successListener.onSuccess(null);
                return;
            }

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

                        List<String> oldCareDays = plant.getCareDays();

                        final List<String> careDays;

                        if (oldCareDays == null) {
                            careDays = new ArrayList<>();
                        } else {
                            careDays = new ArrayList<>(oldCareDays);
                        }

                        // Một ngày chỉ được tính một lần
                        if (!careDays.contains(getToday())) {
                            careDays.add(getToday());
                        }

                        plant.setCareDays(careDays);

                        // Cập nhật ngày chăm sóc của cây
                        myPlantRepository.update(
                                userId,
                                plant,
                                unusedPlant -> {

                                    // Tính chuỗi chăm sóc dài nhất
                                    int longestStreak =
                                            ChuoiUtil.calculateLongestStreak(careDays);

                                    // Lấy thông tin tài khoản
                                    userRepository.findById(
                                            userId,
                                            user -> {

                                                if (user == null) {
                                                    failureListener.onFailure(
                                                            new Exception("Không tìm thấy người dùng")
                                                    );
                                                    return;
                                                }

                                                // Chỉ cập nhật nếu có kỷ lục mới
                                                if (longestStreak > user.getMaxStreakRecord()) {

                                                    userRepository.updateMaxStreakRecord(
                                                            userId,
                                                            longestStreak,
                                                            successListener,
                                                            failureListener
                                                    );

                                                } else {
                                                    successListener.onSuccess(null);
                                                }
                                            },
                                            failureListener
                                    );
                                },
                                failureListener
                        );
                    },
                    failureListener
            );
        }, failureListener);
    }



    // Lấy danh sách nhật ký chăm sóc của cây
    public void getCareLogs(String userId,
                            String myPlantId,
                            OnSuccessListener<List<CareLog>> successListener,
                            OnFailureListener failureListener) {

        careLogRepository.getByMyPlantId(
                userId,
                myPlantId,
                successListener,
                failureListener
        );
    }

    // Cập nhật nhật ký chăm sóc
    // Đã bổ sung userId vào tham số
    public void updateCareLog(String userId,
                              CareLog careLog,
                              OnSuccessListener<Void> successListener,
                              OnFailureListener failureListener) {

        careLogRepository.update(
                userId,
                careLog,
                successListener,
                failureListener
        );
    }

    // Xóa một nhật ký chăm sóc
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

