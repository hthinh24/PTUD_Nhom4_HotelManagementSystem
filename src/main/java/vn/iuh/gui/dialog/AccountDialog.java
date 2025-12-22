package vn.iuh.gui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import vn.iuh.config.SecurityConfig;
import vn.iuh.dao.NhanVienDAO;
import vn.iuh.dao.TaiKhoanDAO;
import vn.iuh.entity.NhanVien;
import vn.iuh.entity.TaiKhoan;
import vn.iuh.gui.base.CustomUI;
import vn.iuh.gui.base.Main;
import vn.iuh.util.AccountUtil;

import javax.swing.*;
import java.awt.*;

public class AccountDialog extends JDialog {

    private static final Font FONT_LABEL = new Font("Arial", Font.BOLD, 14);
    private static final Font FONT_FIELD = new Font("Arial", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Arial", Font.BOLD, 15);

    private JTextField txtMaTK, txtMaNV, txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JComboBox<String> cmbChucVu;
    private JButton btnSave, btnCancel;
    private TaiKhoan taiKhoan;
    private boolean isSaved = false;
    private final boolean isEditMode;

    public AccountDialog(Frame owner, String title, String newMaTaiKhoan, String maNhanVien, String tenNV) {
        super(owner, title, true);
        this.isEditMode = false;
        this.taiKhoan = null;
        AccountUtil accountUtil = new AccountUtil();
        String tenDN = accountUtil.taoTenDangNhap(tenNV);
        init();

        txtMaTK.setText(newMaTaiKhoan);
        txtMaNV.setText(maNhanVien);
        txtTenDangNhap.setText(tenDN);
        txtMatKhau.setText("1");
        txtMaTK.setEnabled(false);
        txtMaNV.setEnabled(false);
        txtTenDangNhap.setEnabled(false);
        txtMatKhau.setEnabled(false);
        txtMaTK.setBackground(Color.decode("#E5E7EB"));
        txtMaNV.setBackground(Color.decode("#E5E7EB"));
        txtTenDangNhap.setBackground(Color.decode("#E5E7EB"));
        txtMatKhau.setBackground(Color.decode("#E5E7EB"));
    }

    public AccountDialog(Frame owner, String title, TaiKhoan existingTaiKhoan) {
        super(owner, title, true);
        this.isEditMode = true;
        this.taiKhoan = existingTaiKhoan;

        init();
        loadData(existingTaiKhoan);
    }
    private void init() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        setBackground(CustomUI.white);

        JLabel lblTitle = new JLabel(getTitle(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(CustomUI.white);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(CustomUI.blue);
        lblTitle.setPreferredSize(new Dimension(0, 50));

        JPanel mainPanel = createMainPanel();
        JPanel buttonPanel = createButtonPanel();
        initEvents();

        add(lblTitle, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getOwner());
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CustomUI.white);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtMaTK = new JTextField();
        txtMaNV = new JTextField();
        txtTenDangNhap = new JTextField();
        txtMatKhau = new JPasswordField();
        cmbChucVu = new JComboBox<>(new String[]{"Lễ tân", "Quản lý", "Admin"});

        styleField(txtMaTK);
        styleField(txtMaNV);
        styleField(txtTenDangNhap);
        styleField(txtMatKhau);
        styleField(cmbChucVu);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, 0, 15, 10);

        GridBagConstraints gbcField = new GridBagConstraints();
        gbcField.gridx = 1;
        gbcField.anchor = GridBagConstraints.WEST;
        gbcField.fill = GridBagConstraints.HORIZONTAL;
        gbcField.weightx = 1.0;
        gbcField.insets = new Insets(0, 0, 15, 0);

        gbc.gridy = 0;
        JLabel lblMaTK = new JLabel("Mã tài khoản:");
        lblMaTK.setFont(FONT_LABEL);
        panel.add(lblMaTK, gbc);

        gbcField.gridy = 0;
        panel.add(txtMaTK, gbcField);

        gbc.gridy = 1;
        JLabel lblMaNV = new JLabel("Mã nhân viên:");
        lblMaNV.setFont(FONT_LABEL);
        panel.add(lblMaNV, gbc);

        gbcField.gridy = 1;
        panel.add(txtMaNV, gbcField);

        gbc.gridy = 2;
        JLabel lblTenDN = new JLabel("Tên đăng nhập:");
        lblTenDN.setFont(FONT_LABEL);
        panel.add(lblTenDN, gbc);

        gbcField.gridy = 2;
        panel.add(txtTenDangNhap, gbcField);

        gbc.gridy = 3;
        JLabel lblMatKhau = new JLabel("Mật khẩu:");
        lblMatKhau.setFont(FONT_LABEL);
        panel.add(lblMatKhau, gbc);

        gbcField.gridy = 3;
        panel.add(txtMatKhau, gbcField);

        gbc.gridy = 4;
        JLabel lblChucVu = new JLabel("Chức vụ:");
        lblChucVu.setFont(FONT_LABEL);
        panel.add(lblChucVu, gbc);

        gbcField.gridy = 4;
        panel.add(cmbChucVu, gbcField);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(CustomUI.white);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#E5E7EB")));

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(FONT_BUTTON);
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setBackground(Color.decode("#DC2626"));
        btnCancel.setPreferredSize(new Dimension(120, 40));
        btnCancel.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        btnSave = new JButton("Lưu lại");
        btnSave.setFont(FONT_BUTTON);
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(Color.decode("#1D4ED8"));
        btnSave.setPreferredSize(new Dimension(120, 40));
        btnSave.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        panel.add(btnCancel);
        panel.add(btnSave);

        return panel;
    }

    private void styleField(JComponent field) {
        field.setFont(FONT_FIELD);
        field.setPreferredSize(new Dimension(350, 40));
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
    }

    private void loadData(TaiKhoan tk) {
        txtMaTK.setText(tk.getMaTaiKhoan());
        txtMaNV.setText(tk.getMaNhanVien());
        txtTenDangNhap.setText(tk.getTenDangNhap());
        txtMatKhau.setText("");
        txtMatKhau.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "******");

        cmbChucVu.setSelectedItem(convertMaChucVuToTen(tk.getMaChucVu()));
        NhanVienDAO nvDao = new NhanVienDAO();
        NhanVien currentUser = nvDao.layNVTheoMaPhienDangNhap(Main.getCurrentLoginSession());
        // Nếu người đang đăng nhập trùng với người được load lên form
        if (currentUser != null && currentUser.getMaNhanVien().equals(tk.getMaNhanVien())) {
            cmbChucVu.setEnabled(false);
            cmbChucVu.setToolTipText("Bạn không thể tự thay đổi chức vụ của chính mình");
        }
        txtMaTK.setEnabled(false);
        txtMaNV.setEnabled(false);
        txtMaTK.setBackground(Color.decode("#E5E7EB"));
        txtMaNV.setBackground(Color.decode("#E5E7EB"));
    }

    private void initEvents() {
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> onCancel());
    }

    private void onCancel() {
        isSaved = false;
        dispose();
    }

    private void onSave() {
        if (!validateInput()) {
            return;
        }
        String selectedRoleName = (String) cmbChucVu.getSelectedItem();
        String targetRoleID = convertTenToMaChucVu(selectedRoleName);

        // Lấy mã chức vụ của BẢN THÂN người đang thao tác
        String currentRoleID = getCurrentLoginedRoleID();

        // So sánh cấp bậc
        int currentLevel = getRoleLevel(currentRoleID);
        int targetLevel = getRoleLevel(targetRoleID);

        // Nếu cấp bậc hiện tại < cấp bậc muốn gán -> CHẶN
        if (currentLevel < targetLevel) {
            JOptionPane.showMessageDialog(this,
                    "Bạn không đủ quyền hạn để cấp chức vụ cao hơn chức vụ hiện tại của bạn!\n" +
                            "Quyền hiện tại: " + convertMaChucVuToTen(currentRoleID) + "\n" +
                            "Quyền muốn cấp: " + selectedRoleName,
                    "Lỗi phân quyền", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isEditMode) {
            this.taiKhoan = new TaiKhoan();
            this.taiKhoan.setMaTaiKhoan(txtMaTK.getText());
            this.taiKhoan.setMaNhanVien(txtMaNV.getText());
        }
        this.taiKhoan.setTenDangNhap(txtTenDangNhap.getText().trim());

        // Chuyển đổi tên Ví d như (Lễ tân) về mã (CV001)
        String selectedRole = (String) cmbChucVu.getSelectedItem();
        this.taiKhoan.setMaChucVu(convertTenToMaChucVu(selectedRole));
        String matKhauMoi = new String(txtMatKhau.getPassword()).trim();
        if (!matKhauMoi.isEmpty()) {
            String matKhauDaMaHoa = SecurityConfig.hashPassword(matKhauMoi);
            this.taiKhoan.setMatKhau(matKhauDaMaHoa);
        }

        isSaved = true;
        dispose();
    }

    private boolean validateInput() {
        String tenDN = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword()).trim();
        if (tenDN.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTenDangNhap.requestFocus();
            return false;
        }

        if (isEditMode && matKhau.isEmpty()) {
            // Đang ở chế độ "Sửa" và mật khẩu để trống -> Hợp lệ (nghĩa là không đổi)

        } else {
            // Đây là chế độ "Thêm" (mật khẩu không được trống hoặc chế độ "Sửa" (người dùng đã nhập mật khẩu mới)
            // Phải validate mật khẩu mới
            if (matKhau.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtMatKhau.requestFocus();
                return false;
            }
//            if (!matKhau.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$")) {
//                JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                txtMatKhau.requestFocus();
//                return false;
//            }
        }

        if (cmbChucVu.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chức vụ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (isEditMode) {
            // --- TRƯỜNG HỢP SỬA ---
            // Nếu mật khẩu TRỐNG -> Hợp lệ (Cho qua, để hàm onSave xử lý giữ pass cũ)
            // Nếu mật khẩu CÓ NHẬP -> Thì mới kiểm tra độ dài (nếu muốn)
            /*
            if (!matKhau.isEmpty() && matKhau.length() < 6) {
                 JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 6 ký tự.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                 txtMatKhau.requestFocus();
                 return false;
            }
            */
        } else {
            // thêm mới mật khẩu
            if (matKhau.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu cho tài khoản mới.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtMatKhau.requestFocus();
                return false;
            }
        }

        return true;
    }
    private String convertMaChucVuToTen(String maChucVu) {
        if (maChucVu == null) return "Lễ tân";
        return switch (maChucVu.trim().toUpperCase()) {
            case "CV001" -> "Lễ tân";
            case "CV002" -> "Quản lý";
            case "CV003" -> "Admin";
            default -> "Lễ tân";
        };
    }

    private String convertTenToMaChucVu(String tenChucVu) {
        if (tenChucVu == null) return "CV001";
        return switch (tenChucVu) {
            case "Lễ tân" -> "CV001";
            case "Quản lý" -> "CV002";
            case "Admin" -> "CV003";
            default -> "CV001";
        };
    }

    private String getCurrentLoginedRoleID() {
        String session = Main.getCurrentLoginSession();
        if (session == null) return null;

        NhanVienDAO nvDao = new NhanVienDAO();
        TaiKhoanDAO tkDao = new TaiKhoanDAO();

        NhanVien nv = nvDao.layNVTheoMaPhienDangNhap(session);
        if (nv == null) return null;

        return tkDao.getChucVuBangMaNhanVien(nv.getMaNhanVien());
    }

    private int getRoleLevel(String maChucVu) {
        if (maChucVu == null) return 0;
        return switch (maChucVu.trim().toUpperCase()) {
            case "CV003" -> 3;
            case "CV002" -> 2;
            case "CV001" -> 1;
            default -> 0;
        };
    }

    public TaiKhoan getTaiKhoan() {
        return this.taiKhoan;
    }

    public boolean isSaved() {
        return this.isSaved;
    }
}