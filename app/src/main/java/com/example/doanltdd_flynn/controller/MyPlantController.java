package com.example.doanltdd_flynn.controller;
import com.example.doanltdd_flynn.model.MyPlant;
import com.example.doanltdd_flynn.model.PlantHistory;
import com.example.doanltdd_flynn.model.PlantSuggestion;
import com.example.doanltdd_flynn.model.StreakSummary;
import com.example.doanltdd_flynn.repository.MyPlantRepository;
import com.example.doanltdd_flynn.repository.PlantHistoryRepository;
import com.example.doanltdd_flynn.repository.PlantSuggestionRepository;
import com.example.doanltdd_flynn.repository.UserRepository;
import com.example.doanltdd_flynn.utils.ChuoiUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyPlantController {

    private MyPlantRepository myPlantRepository;
    private PlantHistoryRepository plantHistoryRepository;
    private UserRepository userRepository;
    private PlantSuggestionRepository plantSuggestionRepository;

    public MyPlantController() {
        myPlantRepository = new MyPlantRepository();
        plantHistoryRepository = new PlantHistoryRepository();
        userRepository = new UserRepository();
        plantSuggestionRepository = new PlantSuggestionRepository();
    }

   public void addMyPlant(MyPlant myPlant,
                           OnSuccessListener<Void> successListener,
                           OnFailureListener failureListener) {

        if (myPlant == null || myPlant.getUserId() == null) {
            failureListener.onFailure(new IllegalArgumentException("Vui lòng đăng nhập để thêm cây"));
            return;
        }
        if (myPlant.getPlantName() == null || myPlant.getPlantName().trim().isEmpty()) {
            failureListener.onFailure(new IllegalArgumentException("Vui lòng nhập hoặc chọn tên cây"));
            return;
        }
        LocalDate planting = ChuoiUtil.toDate(myPlant.getPlantingDate());
        if (planting == null) {
            failureListener.onFailure(new IllegalArgumentException("Vui lòng chọn ngày bắt đầu trồng"));
            return;
        }
        if (planting.isAfter(ChuoiUtil.todayDate())) {
            failureListener.onFailure(new IllegalArgumentException("Ngày bắt đầu trồng không được ở tương lai"));
            return;
        }

        myPlant.setPlantName(myPlant.getPlantName().trim());
        myPlant.setCareDays(new ArrayList<>()); // ngày trồng ở quá khứ không tạo ngày chăm

        myPlantRepository.insert(
                myPlant,
                successListener,
                failureListener
        );
    }

    // Lấy danh sách cây của người dùng (cây "chờ chăm" được đưa lên đầu)
    public void getMyPlants(String userId,
                            OnSuccessListener<List<MyPlant>> successListener,
                            OnFailureListener failureListener) {

        myPlantRepository.getByUserId(
                userId,
                list -> successListener.onSuccess(ChuoiUtil.sortForDisplay(list)),
                failureListener
        );
    }

    public void getStreakSummary(String userId,
                                 OnSuccessListener<StreakSummary> successListener,
                                 OnFailureListener failureListener) {

        myPlantRepository.getByUserId(userId, plants ->
                        userRepository.getMaxStreakRecord(userId, record ->
                                        successListener.onSuccess(ChuoiUtil.summarize(plants, record)),
                                failureListener),
                failureListener);
    }

    // THÊM MỚI: kỷ lục chuỗi của tài khoản
    public void getMaxStreakRecord(String userId,
                                   OnSuccessListener<Integer> successListener,
                                   OnFailureListener failureListener) {

        userRepository.getMaxStreakRecord(userId, successListener, failureListener);
    }

    // THÊM MỚI: danh sách đề xuất khi thêm cây (Bách khoa hoặc danh sách mặc định)
    public void getPlantSuggestions(OnSuccessListener<List<PlantSuggestion>> successListener,
                                    OnFailureListener failureListener) {

        plantSuggestionRepository.getSuggestions(successListener, failureListener);
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

    // Lấy lịch sử cây đã xóa (mới xóa lên đầu)
    public void getHistory(String userId,
                           OnSuccessListener<List<PlantHistory>> successListener,
                           OnFailureListener failureListener) {

        plantHistoryRepository.getByUserId(
                userId,
                list -> {
                    Collections.sort(list, (a, b) -> {
                        String da = a.getDeletedAt() == null ? "" : a.getDeletedAt();
                        String db = b.getDeletedAt() == null ? "" : b.getDeletedAt();
                        return db.compareTo(da);
                    });
                    successListener.onSuccess(list);
                },
                failureListener
        );
    }

    public void deleteMyPlant(String userId,
                              String myPlantId,
                              OnSuccessListener<Void> successListener,
                              OnFailureListener failureListener) {

        myPlantRepository.findById(userId, myPlantId, plant -> {

            if (plant == null) {
                failureListener.onFailure(
                        new Exception("Không tìm thấy cây cần xóa"));
                return;
            }

            String deletedAt = ChuoiUtil.today();
            int companionDays = ChuoiUtil.calculateCompanionDays(
                    plant.getPlantingDate(), deletedAt);

            if (companionDays <= 0) {
                failureListener.onFailure(
                        new Exception("Ngày trồng không hợp lệ"));
                return;
            }

            int longestStreak = ChuoiUtil.calculateLongestStreak(plant.getCareDays());

            PlantHistory history = new PlantHistory(
                    null,
                    userId,
                    plant.getPlantName(),
                    plant.getImageUrl(),
                    companionDays,
                    longestStreak,
                    deletedAt
            );

            myPlantRepository.deleteWithHistory(
                    userId, myPlantId, history,
                    successListener, failureListener);

        }, failureListener);
    }
}
