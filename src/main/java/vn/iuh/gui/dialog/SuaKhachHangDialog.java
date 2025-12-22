package vn.iuh.gui.dialog;

import vn.iuh.entity.KhachHang;
import vn.iuh.service.CustomerService;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;


public class SuaKhachHangDialog extends JDialog {

    private final CustomerService customerService;
    private final String maKhachHang;
    private final Runnable onSuccess; // callback khi sửa thành công

    private JLabel lblMa;
    private JTextField txtTen;
    private JTextField txtCCCD;
    private JTextField txtPhone;

    // lưu thông tin gốc để so sánh thay đổi
    private KhachHang originalKhach;

    public SuaKhachHangDialog(Window owner, CustomerService customerService, String maKhachHang, Runnable onSuccess) {
        super(owner, "Sửa khách hàng", ModalityType.APPLICATION_MODAL);
        this.customerService = customerService;
        this.maKhachHang = maKhachHang;
        this.onSuccess = onSuccess;

        initUI();
        loadData();
        pack();
        setSize(520, 300);
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(12, 12));
        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        lblMa = new JLabel("-");
        txtTen = new JTextField();
        txtCCCD = new JTextField();
        txtPhone = new JTextField();

        // Row 0: Mã (label)
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        pnlMain.add(new JLabel("Mã khách hàng:"), gc);
        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1.0;
        pnlMain.add(lblMa, gc);

        // Row 1: Tên
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        pnlMain.add(new JLabel("Họ tên (*):"), gc);
        gc.gridx = 1; gc.gridy = 1; gc.weightx = 1.0;
        pnlMain.add(txtTen, gc);

        // Row 2: CCCD
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0;
        pnlMain.add(new JLabel("CCCD:"), gc);
        gc.gridx = 1; gc.gridy = 2; gc.weightx = 1.0;
        pnlMain.add(txtCCCD, gc);

        // Row 3: Điện thoại
        gc.gridx = 0; gc.gridy = 3; gc.weightx = 0;
        pnlMain.add(new JLabel("Điện thoại:"), gc);
        gc.gridx = 1; gc.gridy = 3; gc.weightx = 1.0;
        pnlMain.add(txtPhone, gc);

        add(pnlMain, BorderLayout.CENTER);

        // bottom: buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        bottom.add(btnCancel);
        bottom.add(btnSave);

        add(bottom, BorderLayout.SOUTH);

        // Enter => Save, Escape => Close
        getRootPane().setDefaultButton(btnSave);
        // add escape
        KeyStroke esc = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(esc, "ESC");
        getRootPane().getActionMap().put("ESC", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { dispose(); }
        });
    }

    private void loadData() {
        try {
            if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã khách hàng không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            KhachHang kh = customerService.getCustomerByID(maKhachHang);
            if (kh == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với mã: " + maKhachHang, "Lỗi", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            // lưu bản gốc để so sánh
            originalKhach = kh;

            lblMa.setText(kh.getMaKhachHang());
            txtTen.setText(kh.getTenKhachHang() != null ? kh.getTenKhachHang() : "");
            txtCCCD.setText(kh.getCCCD() != null ? kh.getCCCD() : "");
            txtPhone.setText(kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void onSave() {
        String ten = txtTen.getText() != null ? txtTen.getText().trim() : "";
        String cccd = txtCCCD.getText() != null ? txtCCCD.getText().trim() : "";
        String phone = txtPhone.getText() != null ? txtPhone.getText().trim() : "";

        // Validate giống dialog thêm khách hàng
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không được để trống.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtTen.requestFocus();
            return;
        }

        // Validate CCCD: bắt buộc, đúng 12 chữ số, bắt đầu bằng 0 hoặc 1
        if (cccd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số căn cước (12 chữ số, bắt đầu bằng 0 hoặc 1)", "Lỗi", JOptionPane.WARNING_MESSAGE);
            txtCCCD.requestFocus();
            return;
        }
        if (!cccd.matches("^[01]\\d{11}$")) {
            JOptionPane.showMessageDialog(this, "CCCD không hợp lệ. CCCD phải gồm đúng 12 chữ số và bắt đầu bằng 0 hoặc 1.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            txtCCCD.requestFocus();
            return;
        }

        // Validate phone: bắt buộc, đúng 10 chữ số, bắt đầu bằng 09, 03, 05, 02, 07
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại (10 chữ số)", "Lỗi", JOptionPane.WARNING_MESSAGE);
            txtPhone.requestFocus();
            return;
        }
        if (!phone.matches("^(09|03|05|02|07)\\d{8}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ. Phải có 10 chữ số và bắt đầu bằng một trong các đầu: 09, 03, 05, 02, 07.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            txtPhone.requestFocus();
            return;
        }

        // Kiểm tra có thay đổi dữ liệu hay không
        if (originalKhach != null) {
            String origTen = originalKhach.getTenKhachHang() != null ? originalKhach.getTenKhachHang().trim() : "";
            String origCCCD = originalKhach.getCCCD() != null ? originalKhach.getCCCD().trim() : "";
            String origPhone = originalKhach.getSoDienThoai() != null ? originalKhach.getSoDienThoai().trim() : "";

            boolean changed = !Objects.equals(origTen, ten) || !Objects.equals(origCCCD, cccd) || !Objects.equals(origPhone, phone);
            if (!changed) {
                JOptionPane.showMessageDialog(this, "Không có thay đổi nào để cập nhật.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                return;
            }
        }

        // build entity
        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(maKhachHang);
        kh.setTenKhachHang(ten);
        kh.setCCCD(cccd != null && !cccd.isEmpty() ? cccd : null);
        kh.setSoDienThoai(phone != null && !phone.isEmpty() ? phone : null);

        try {
            KhachHang updated = customerService.updateCustomerV2(kh);
            if (updated != null) {
                JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (onSuccess != null) {
                    try { onSuccess.run(); } catch (Exception ex) { ex.printStackTrace(); }
                }
            } else {
                // Service có thể trả null khi không cập nhật (tùy impl)
                JOptionPane.showMessageDialog(this, "Cập nhật hoàn tất.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (onSuccess != null) {
                    try { onSuccess.run(); } catch (Exception ex) { ex.printStackTrace(); }
                }
            }
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this, iae.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalStateException ise) {
            JOptionPane.showMessageDialog(this, ise.getMessage(), "Không thể cập nhật", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
