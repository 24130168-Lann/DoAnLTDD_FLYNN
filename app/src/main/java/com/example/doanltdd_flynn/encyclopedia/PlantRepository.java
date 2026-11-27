package com.example.doanltdd_flynn.encyclopedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlantRepository {

    public static final String COLLECTION_NAME = "cay_bach_khoa";

    public interface PlantListCallback {
        void onSuccess(@NonNull List<Plant> plants);
        void onError(@NonNull Exception e);
    }

    public interface PlantDetailCallback {
        void onSuccess(@NonNull Plant plant);
        void onNotFound();
        void onError(@NonNull Exception e);
    }

    private final FirebaseFirestore db;

    public PlantRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Lấy toàn bộ danh sách cây trong collection cay_bach_khoa.
     */
    public void getAllPlants(@NonNull PlantListCallback callback) {
        db.collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Plant> result = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Plant plant = mapDocumentToPlant(doc);
                        if (plant != null) {
                            result.add(plant);
                        }
                    }
                    callback.onSuccess(result);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lọc cây theo danh mục (nhom_cay). Nếu category là null hoặc rỗng hoặc "Tất cả" thì trả về tất cả.
     */
    public void getPlantsByCategory(@Nullable String category, @NonNull PlantListCallback callback) {
        if (category == null || category.trim().isEmpty() || "Tất cả".equalsIgnoreCase(category.trim())) {
            getAllPlants(callback);
            return;
        }

        String trimmedCategory = category.trim();
        db.collection(COLLECTION_NAME)
                .whereEqualTo("nhom_cay", trimmedCategory)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Plant> result = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Plant plant = mapDocumentToPlant(doc);
                        if (plant != null) {
                            result.add(plant);
                        }
                    }
                    callback.onSuccess(result);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Tìm kiếm cây theo từ khóa (khớp không phân biệt hoa thường và bỏ dấu tiếng Việt)
     * trên tên tiếng Việt, tên tiếng Anh, tên khoa học và nhóm cây.
     */
    public void searchPlants(@Nullable String keyword, @Nullable String categoryFilter, @NonNull PlantListCallback callback) {
        getPlantsByCategory(categoryFilter, new PlantListCallback() {
            @Override
            public void onSuccess(@NonNull List<Plant> plants) {
                if (keyword == null || keyword.trim().isEmpty()) {
                    callback.onSuccess(plants);
                    return;
                }

                String normalizedQuery = normalizeText(keyword);
                List<Plant> filtered = new ArrayList<>();
                for (Plant plant : plants) {
                    String combined = safe(plant.getTenTiengViet()) + " "
                            + safe(plant.getTenTiengAnh()) + " "
                            + safe(plant.getTenKhoaHoc()) + " "
                            + safe(plant.getNhomCay());

                    if (normalizeText(combined).contains(normalizedQuery)) {
                        filtered.add(plant);
                    }
                }
                callback.onSuccess(filtered);
            }

            @Override
            public void onError(@NonNull Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * Lấy chi tiết một cây theo document ID trong collection cay_bach_khoa.
     */
    public void getPlantById(@NonNull String plantId, @NonNull PlantDetailCallback callback) {
        if (plantId.trim().isEmpty()) {
            callback.onNotFound();
            return;
        }

        db.collection(COLLECTION_NAME)
                .document(plantId.trim())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot == null || !documentSnapshot.exists()) {
                        callback.onNotFound();
                        return;
                    }
                    Plant plant = mapDocumentToPlant(documentSnapshot);
                    if (plant == null) {
                        callback.onNotFound();
                        return;
                    }
                    callback.onSuccess(plant);
                })
                .addOnFailureListener(callback::onError);
    }

    @Nullable
    private Plant mapDocumentToPlant(@NonNull DocumentSnapshot doc) {
        Plant plant = doc.toObject(Plant.class);
        if (plant == null) {
            return null;
        }
        // Đảm bảo luôn có id ngay cả khi field "id" bên trong document bị bỏ trống
        if (plant.getId() == null || plant.getId().trim().isEmpty()) {
            plant.setId(doc.getId());
        }
        return plant;
    }

    @NonNull
    private String safe(@Nullable String value) {
        return value == null ? "" : value;
    }

    @NonNull
    private String normalizeText(@NonNull String input) {
        String lower = input.toLowerCase(Locale.ROOT).trim();
        String temp = Normalizer.normalize(lower, Normalizer.Form.NFD);
        return temp.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'd');
    }
}