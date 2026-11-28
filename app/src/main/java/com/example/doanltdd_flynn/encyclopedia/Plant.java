package com.example.doanltdd_flynn.encyclopedia;

import com.google.firebase.firestore.PropertyName;

public class Plant {

    private String id;
    private String nhom_cay;
    private String layout_concept;
    private String mau_chu_dao;
    private String ten_tieng_viet;
    private String ten_tieng_anh;
    private String ten_khoa_hoc;
    private String cau_dan_ngan;
    private String gioi_thieu;
    private String nguon_goc;
    private String thoi_diem_dep;
    private String anh_sang;
    private int chu_ky_tuoi_ngay;

    private String anh_chinh;
    private String anh_gioi_thieu;
    private String anh_bo_phan;

    private String dac_diem_tieu_de;
    private String dac_diem_noi_dung;

    private String muc_2_ten_tab;
    private String muc_2_tieu_de;
    private String muc_2_noi_dung;

    private String muc_3_ten_tab;
    private String muc_3_tieu_de;
    private String muc_3_noi_dung;

    private String muc_4_ten_tab;
    private String muc_4_tieu_de;
    private String muc_4_noi_dung;

    // Constructor rỗng bắt buộc để Firestore DocumentSnapshot.toObject(Plant.class) hoạt động
    public Plant() {
    }

    @PropertyName("id")
    public String getId() {
        return id;
    }

    @PropertyName("id")
    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("nhom_cay")
    public String getNhomCay() {
        return nhom_cay;
    }

    @PropertyName("nhom_cay")
    public void setNhomCay(String nhom_cay) {
        this.nhom_cay = nhom_cay;
    }

    @PropertyName("layout_concept")
    public String getLayoutConcept() {
        return layout_concept;
    }

    @PropertyName("layout_concept")
    public void setLayoutConcept(String layout_concept) {
        this.layout_concept = layout_concept;
    }

    @PropertyName("mau_chu_dao")
    public String getMauChuDao() {
        return mau_chu_dao;
    }

    @PropertyName("mau_chu_dao")
    public void setMauChuDao(String mau_chu_dao) {
        this.mau_chu_dao = mau_chu_dao;
    }

    @PropertyName("ten_tieng_viet")
    public String getTenTiengViet() {
        return ten_tieng_viet;
    }

    @PropertyName("ten_tieng_viet")
    public void setTenTiengViet(String ten_tieng_viet) {
        this.ten_tieng_viet = ten_tieng_viet;
    }

    @PropertyName("ten_tieng_anh")
    public String getTenTiengAnh() {
        return ten_tieng_anh;
    }

    @PropertyName("ten_tieng_anh")
    public void setTenTiengAnh(String ten_tieng_anh) {
        this.ten_tieng_anh = ten_tieng_anh;
    }

    @PropertyName("ten_khoa_hoc")
    public String getTenKhoaHoc() {
        return ten_khoa_hoc;
    }

    @PropertyName("ten_khoa_hoc")
    public void setTenKhoaHoc(String ten_khoa_hoc) {
        this.ten_khoa_hoc = ten_khoa_hoc;
    }

    @PropertyName("cau_dan_ngan")
    public String getCauDanNgan() {
        return cau_dan_ngan;
    }

    @PropertyName("cau_dan_ngan")
    public void setCauDanNgan(String cau_dan_ngan) {
        this.cau_dan_ngan = cau_dan_ngan;
    }

    @PropertyName("gioi_thieu")
    public String getGioiThieu() {
        return gioi_thieu;
    }

    @PropertyName("gioi_thieu")
    public void setGioiThieu(String gioi_thieu) {
        this.gioi_thieu = gioi_thieu;
    }

    @PropertyName("nguon_goc")
    public String getNguonGoc() {
        return nguon_goc;
    }

    @PropertyName("nguon_goc")
    public void setNguonGoc(String nguon_goc) {
        this.nguon_goc = nguon_goc;
    }

    @PropertyName("thoi_diem_dep")
    public String getThoiDiemDep() {
        return thoi_diem_dep;
    }

    @PropertyName("thoi_diem_dep")
    public void setThoiDiemDep(String thoi_diem_dep) {
        this.thoi_diem_dep = thoi_diem_dep;
    }

    @PropertyName("anh_sang")
    public String getAnhSang() {
        return anh_sang;
    }

    @PropertyName("anh_sang")
    public void setAnhSang(String anh_sang) {
        this.anh_sang = anh_sang;
    }

    @PropertyName("chu_ky_tuoi_ngay")
    public int getChuKyTuoiNgay() {
        return chu_ky_tuoi_ngay;
    }

    @PropertyName("chu_ky_tuoi_ngay")
    public void setChuKyTuoiNgay(int chu_ky_tuoi_ngay) {
        this.chu_ky_tuoi_ngay = chu_ky_tuoi_ngay;
    }

    @PropertyName("anh_chinh")
    public String getAnhChinh() {
        return anh_chinh;
    }

    @PropertyName("anh_chinh")
    public void setAnhChinh(String anh_chinh) {
        this.anh_chinh = anh_chinh;
    }

    @PropertyName("anh_gioi_thieu")
    public String getAnhGioiThieu() {
        return anh_gioi_thieu;
    }

    @PropertyName("anh_gioi_thieu")
    public void setAnhGioiThieu(String anh_gioi_thieu) {
        this.anh_gioi_thieu = anh_gioi_thieu;
    }

    @PropertyName("anh_bo_phan")
    public String getAnhBoPhan() {
        return anh_bo_phan;
    }

    @PropertyName("anh_bo_phan")
    public void setAnhBoPhan(String anh_bo_phan) {
        this.anh_bo_phan = anh_bo_phan;
    }

    @PropertyName("dac_diem_tieu_de")
    public String getDacDiemTieuDe() {
        return dac_diem_tieu_de;
    }

    @PropertyName("dac_diem_tieu_de")
    public void setDacDiemTieuDe(String dac_diem_tieu_de) {
        this.dac_diem_tieu_de = dac_diem_tieu_de;
    }

    @PropertyName("dac_diem_noi_dung")
    public String getDacDiemNoiDung() {
        return dac_diem_noi_dung;
    }

    @PropertyName("dac_diem_noi_dung")
    public void setDacDiemNoiDung(String dac_diem_noi_dung) {
        this.dac_diem_noi_dung = dac_diem_noi_dung;
    }

    @PropertyName("muc_2_ten_tab")
    public String getMuc2TenTab() {
        return muc_2_ten_tab;
    }

    @PropertyName("muc_2_ten_tab")
    public void setMuc2TenTab(String muc_2_ten_tab) {
        this.muc_2_ten_tab = muc_2_ten_tab;
    }

    @PropertyName("muc_2_tieu_de")
    public String getMuc2TieuDe() {
        return muc_2_tieu_de;
    }

    @PropertyName("muc_2_tieu_de")
    public void setMuc2TieuDe(String muc_2_tieu_de) {
        this.muc_2_tieu_de = muc_2_tieu_de;
    }

    @PropertyName("muc_2_noi_dung")
    public String getMuc2NoiDung() {
        return muc_2_noi_dung;
    }

    @PropertyName("muc_2_noi_dung")
    public void setMuc2NoiDung(String muc_2_noi_dung) {
        this.muc_2_noi_dung = muc_2_noi_dung;
    }

    @PropertyName("muc_3_ten_tab")
    public String getMuc3TenTab() {
        return muc_3_ten_tab;
    }

    @PropertyName("muc_3_ten_tab")
    public void setMuc3TenTab(String muc_3_ten_tab) {
        this.muc_3_ten_tab = muc_3_ten_tab;
    }

    @PropertyName("muc_3_tieu_de")
    public String getMuc3TieuDe() {
        return muc_3_tieu_de;
    }

    @PropertyName("muc_3_tieu_de")
    public void setMuc3TieuDe(String muc_3_tieu_de) {
        this.muc_3_tieu_de = muc_3_tieu_de;
    }

    @PropertyName("muc_3_noi_dung")
    public String getMuc3NoiDung() {
        return muc_3_noi_dung;
    }

    @PropertyName("muc_3_noi_dung")
    public void setMuc3NoiDung(String muc_3_noi_dung) {
        this.muc_3_noi_dung = muc_3_noi_dung;
    }

    @PropertyName("muc_4_ten_tab")
    public String getMuc4TenTab() {
        return muc_4_ten_tab;
    }

    @PropertyName("muc_4_ten_tab")
    public void setMuc4TenTab(String muc_4_ten_tab) {
        this.muc_4_ten_tab = muc_4_ten_tab;
    }

    @PropertyName("muc_4_tieu_de")
    public String getMuc4TieuDe() {
        return muc_4_tieu_de;
    }

    @PropertyName("muc_4_tieu_de")
    public void setMuc4TieuDe(String muc_4_tieu_de) {
        this.muc_4_tieu_de = muc_4_tieu_de;
    }

    @PropertyName("muc_4_noi_dung")
    public String getMuc4NoiDung() {
        return muc_4_noi_dung;
    }

    @PropertyName("muc_4_noi_dung")
    public void setMuc4NoiDung(String muc_4_noi_dung) {
        this.muc_4_noi_dung = muc_4_noi_dung;
    }
}