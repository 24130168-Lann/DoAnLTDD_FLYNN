
package com.example.doanltdd_flynn.controller;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import com.example.doanltdd_flynn.utils.ChuoiUtil;
import com.example.doanltdd_flynn.model.MyPlant;
import com.example.doanltdd_flynn.model.PlantHistory;
import com.example.doanltdd_flynn.repository.MyPlantRepository;
import com.example.doanltdd_flynn.repository.PlantHistoryRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.example.doanltdd_flynn.repository.CareLogRepository;
import java.util.List;

public class MyPlantController {

    private MyPlantRepository myPlantRepository;
    private PlantHistoryRepository plantHistoryRepository;
    private CareLogRepository careLogRepository;
    public MyPlantController() {
        myPlantRepository = new MyPlantRepository();
        plantHistoryRepository = new PlantHistoryRepository();
        careLogRepository = new CareLogRepository();
    }

    // Thêm cây cá nhân
    public void addMyPlant(MyPlant myPlant,
                           OnSuccessListener<Void> successListener,
                           OnFailureListener failureListener) {

        myPlantRepository.insert(
                myPlant,
                successListener,
                failureListener
        );
    }

    // Lấy danh sách cây của người dùng
    public void getMyPlants(String userId,
                            OnSuccessListener<List<MyPlant>> successListener,
                            OnFailureListener failureListener) {

        myPlantRepository.getByUserId(
                userId,
                successListener,
                failureListener
        );
    }

    // Lấy chi tiết một cây
    public void getMyPlantDetail(String userId,
                                 String myPlantId,
                                 OnSuccessListener<MyPlant> successListener,
                                 OnFailureListener failureListener) {

        myPlantRepository.findById(
                userId,
                myPlantId,
                successListener,
                failureListener
        );
    }

    // Cập nhật thông tin cây
    public void updateMyPlant(MyPlant myPlant,
                              OnSuccessListener<Void> successListener,
                              OnFailureListener failureListener) {

        myPlantRepository.update(
                myPlant.getUserId(),
                myPlant,
                successListener,
                failureListener
        );
    }

    // Lấy lịch sử cây đã xóa
    public void getHistory(String userId,
                           OnSuccessListener<List<PlantHistory>> successListener,
                           OnFailureListener failureListener) {

        plantHistoryRepository.getByUserId(
                userId,
                successListener,
                failureListener
        );
    }
    // Xóa cây cá nhân và lưu lại lịch sử
    public void deleteMyPlant(String userId,
                              String myPlantId,
                              OnSuccessListener<Void> successListener,
                              OnFailureListener failureListener) {

        // Bước 1: Tìm cây cần xóa
        myPlantRepository.findById(userId, myPlantId, plant -> {

            if (plant == null) {
                failureListener.onFailure(
                        new Exception("Không tìm thấy cây cần xóa"));
                return;
            }

            // Bước 2: Lấy ngày xóa và tính số ngày đồng hành
            String deletedAt = getDeletedAt();

            int companionDays = calculateCompanionDays(
                    plant.getPlantingDate(),
                    deletedAt
            );

            if (companionDays <= 0) {
                failureListener.onFailure(
                        new Exception("Ngày trồng không hợp lệ"));
                return;
            }

            // Bước 3: Tính chuỗi chăm sóc dài nhất
            int longestStreak = ChuoiUtil.calculateLongestStreak(
                    plant.getCareDays()
            );

            // Bước 4: Tạo lịch sử cây
            PlantHistory history = new PlantHistory(
                    null,
                    userId,
                    plant.getPlantName(),
                    plant.getImageUrl(),
                    companionDays,
                    longestStreak,
                    deletedAt
            );

            // Bước 5: Lưu lịch sử
            plantHistoryRepository.insert(history, unused -> {

                // Bước 6: Xóa toàn bộ nhật ký chăm sóc
                careLogRepository.deleteAllByMyPlantId(
                        userId,
                        myPlantId,
                        unusedLogs -> {

                            // Bước 7: Xóa cây
                            myPlantRepository.delete(
                                    userId,
                                    myPlantId,
                                    successListener,
                                    failureListener
                            );

                        },
                        failureListener
                );

            }, failureListener);

        }, failureListener);
    }
    private int calculateCompanionDays(String plantingDate, String deletedAt) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy/MM/dd", Locale.getDefault());

            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            sdf.setLenient(false);

            Date startDate = sdf.parse(plantingDate);
            Date endDate = sdf.parse(deletedAt);
            if (startDate == null || endDate == null) {
                return 0;
            }

            long difference = endDate.getTime() - startDate.getTime();

            return (int) (difference / (24 * 60 * 60 * 1000)) + 1;

        } catch (Exception e) {
            return 0;
        }
    }
    private String getDeletedAt() {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy/MM/dd", Locale.getDefault());

        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        return sdf.format(new Date());
    }
}

