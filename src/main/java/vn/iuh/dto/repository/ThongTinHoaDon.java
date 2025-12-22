package vn.iuh.dto.repository;

import vn.iuh.entity.KhachHang;
import vn.iuh.entity.NhanVien;
import vn.iuh.entity.TaiKhoan;

public class ThongTinHoaDon {
    private String maHoaDon;
    private String kieuHoaDon;
    private String taiKhoan;
    private String khachHang;
    private String maDonDatPhong;

    public ThongTinHoaDon(String maHoaDon, String kieuHoaDon, String maDonDatPhong, String khachHang, String taiKhoan) {
        this.maHoaDon = maHoaDon;
        this.kieuHoaDon = kieuHoaDon;
        this.maDonDatPhong = maDonDatPhong;
        this.khachHang = khachHang;
        this.taiKhoan = taiKhoan;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getKieuHoaDon() {
        return kieuHoaDon;
    }

    public void setKieuHoaDon(String kieuHoaDon) {
        this.kieuHoaDon = kieuHoaDon;
    }

    public String getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(String taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(String khachHang) {
        this.khachHang = khachHang;
    }

    public String getMaDonDatPhong() {
        return maDonDatPhong;
    }

    public void setMaDonDatPhong(String maDonDatPhong) {
        this.maDonDatPhong = maDonDatPhong;
    }
}
