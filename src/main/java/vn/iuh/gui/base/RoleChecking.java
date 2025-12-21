package vn.iuh.gui.base;


import vn.iuh.constraint.UserRole;
import vn.iuh.dao.NhanVienDAO;
import vn.iuh.dao.TaiKhoanDAO;
import vn.iuh.entity.NhanVien;

import javax.swing.*;
import java.awt.*;

public abstract class RoleChecking extends JPanel{

    public RoleChecking() {
        super(new BorderLayout());
        setBackground(CustomUI.white);
    }

    public final void checkRoleAndLoadData() {
        this.removeAll();
        try {
            buildAdminUI();
        } catch (Exception e) {
            // Bỏ qua lỗi nếu việc build UI có vấn đề, ưu tiên logic check quyền bên dưới
            e.printStackTrace();
        }

        UserRole vaiTro = getCurrentUserRole();

        if (checkAccess(vaiTro)) {
            // NẾU CÓ QUYỀN:
            // Giữ nguyên giao diện vừa build ở trên, chỉ cần repaint lại
            revalidate();
            repaint();
        } else {
            // NẾU KHÔNG CÓ QUYỀN:
            // Xóa sạch giao diện vừa build (dù biến đã new nhưng sẽ không hiển thị)
            removeAll();
            setLayout(new BorderLayout());
            add(createAccessDeniedPanel(), BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }

    protected boolean checkAccess(UserRole vaiTro) {
        return vaiTro == UserRole.QUAN_LY || vaiTro == UserRole.ADMIN;
    }

    private UserRole getCurrentUserRole() {
        TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
        NhanVienDAO nhanVienDAO = new NhanVienDAO();

        String maPhienDangNhap = Main.getCurrentLoginSession();
        if (maPhienDangNhap == null) return UserRole.KHONG_XAC_DINH;

        NhanVien nv = nhanVienDAO.layNVTheoMaPhienDangNhap(maPhienDangNhap);
        if (nv == null) return UserRole.KHONG_XAC_DINH;

        String role = taiKhoanDAO.getChucVuBangMaNhanVien(nv.getMaNhanVien());
        if (role != null) {
            return UserRole.fromString(role.trim());
        }

        return UserRole.KHONG_XAC_DINH;
    }

    private String convertVaiTroToTen(UserRole vaiTro) {
        if (vaiTro == null) return "Lễ tân";
        return switch (vaiTro) {
            case LE_TAN -> "Lễ tân";
            case QUAN_LY -> "Quản lý";
            case ADMIN -> "Admin";
            default -> "Lễ tân";
        };
    }

    private JPanel createAccessDeniedPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        UserRole vaiTro = getCurrentUserRole();
        String vaiTro1 = convertVaiTroToTen(vaiTro);
        JLabel lblThongBao = new JLabel(vaiTro1 + " không có quyền truy cập chức năng này.");
        lblThongBao.setFont(new Font("Arial", Font.BOLD, 18));
        lblThongBao.setHorizontalAlignment(SwingConstants.CENTER);
        lblThongBao.setForeground(Color.RED);
        panel.add(lblThongBao, BorderLayout.CENTER);
        return panel;
    }



    protected abstract void buildAdminUI();
}
