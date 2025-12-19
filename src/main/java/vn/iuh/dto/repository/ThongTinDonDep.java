package vn.iuh.dto.repository;

import java.sql.Timestamp;

public class ThongTinDonDep {
    private String maPhong;
    private String tenPhong;
    private Timestamp thoiGianBatDau;
    private Timestamp thoiGianKetThuc;

    public ThongTinDonDep(String maPhong, String tenPhong, Timestamp thoiGianBatDau, Timestamp thoiGianKetThuc) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
    }

    public String getMaPhong() {
        return maPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public Timestamp getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public Timestamp getThoiGianKetThuc() {
        return thoiGianKetThuc;
    }
}
