package vn.iuh.service.impl;

import com.mchange.v2.lang.StringUtils;
import vn.iuh.constraint.Fee;
import vn.iuh.constraint.InvoiceType;
import vn.iuh.dao.*;
import vn.iuh.dto.event.create.InvoiceCreationEvent;
import vn.iuh.dto.repository.ThongTinPhuPhi;
import vn.iuh.dto.response.InvoiceResponse;
import vn.iuh.entity.*;
//import vn.iuh.gui.dialog.InvoiceDialog;
import vn.iuh.gui.base.Main;
import vn.iuh.gui.dialog.InvoiceDialog2;
import vn.iuh.service.HoaDonService;
import vn.iuh.util.FeeValue;

import javax.swing.*;
import java.util.List;

public class HoaDonServiceImpl implements HoaDonService {
    private final HoaDonDAO hoaDonDAO;
    private final ChiTietHoaDonDAO chiTietHoaDonDAO;
    private final PhongTinhPhuPhiDAO phongTinhPhuPhiDAO;
    private final DonGoiDichVuDao phongDungDichVuDAO;
    private final KhachHangDAO khachHangDAO;
    private final NhanVienDAO nhanVienDAO;
    private final DatPhongDAO datPhongDAO;
    private final DonGoiDichVuDao donGoiDichVuDAO;


    public HoaDonServiceImpl() {
        this.hoaDonDAO = new HoaDonDAO();
        this.chiTietHoaDonDAO = new ChiTietHoaDonDAO();
        this.phongTinhPhuPhiDAO = new PhongTinhPhuPhiDAO();
        this.phongDungDichVuDAO = new DonGoiDichVuDao();
        this.khachHangDAO = new KhachHangDAO();
        this.nhanVienDAO = new NhanVienDAO();
        this.donGoiDichVuDAO = new DonGoiDichVuDao();
        this.datPhongDAO = new DatPhongDAO();
    }

    @Override
    public HoaDon getInvoiceByID(String id) {

        return null;
    }

    @Override
    public InvoiceResponse createInvoice(InvoiceResponse event) {
        SwingUtilities.invokeLater(() -> {
            InvoiceDialog2 dialog = new InvoiceDialog2(event);
            dialog.setVisible(true);
        });
        return event;
    }

    @Override
    public HoaDon getLatestInvoice() {
        return null;
    }

    @Override
    public List<ChiTietHoaDon> insertListChiTietHoaDon(List<ChiTietHoaDon> chiTietHoaDonList) {
        return List.of();
    }

    @Override
    public InvoiceResponse showInvoiceDetails(String reservationId, InvoiceType invoiceType){
        try{
            HoaDon hoaDon = hoaDonDAO.timHoaTheoMaDonDatPhong(reservationId, invoiceType.getStatus());

            String maDonDatPhong = hoaDon.getMaDonDatPhong();

            ThongTinPhuPhi ttpp = FeeValue.getInstance().get(Fee.THUE);

            DonDatPhong ddp = datPhongDAO.getDonDatPhongById(maDonDatPhong);

            KhachHang khachHang = khachHangDAO.timKhachHang(hoaDon.getMaKhachHang());

            NhanVien nhanVien = nhanVienDAO.layNVTheoMaPhienDangNhap(Main.getCurrentLoginSession());

            List<ChiTietHoaDon> dsChiTiet = chiTietHoaDonDAO.layChiTietHoaDonBangMaHoaDon(hoaDon.getMaHoaDon());

            List<PhongDungDichVu> dsPhongDungDichVu = donGoiDichVuDAO.timDonGoiDichVuBangDonDatPhong(hoaDon.getMaDonDatPhong());

            List<PhongTinhPhuPhi> dsPhongTinhPhuPhi = phongTinhPhuPhiDAO.getPhuPhiTheoMaHoaDon(hoaDon.getMaHoaDon());

            return new InvoiceResponse(
                    hoaDon.getMaPhienDangNhap(),
                    ddp,
                    khachHang,
                    hoaDon,
                    nhanVien,
                    dsChiTiet,
                    dsPhongDungDichVu,
                    dsPhongTinhPhuPhi
            );

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
