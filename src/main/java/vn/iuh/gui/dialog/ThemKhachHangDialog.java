package vn.iuh.gui.dialog;

import vn.iuh.entity.KhachHang;
import vn.iuh.service.CustomerService;

import javax.swing.*;
import java.awt.*;

public class ThemKhachHangDialog extends JDialog {

    private final CustomerService customerService;
    private final Runnable onSuccess;

    private JTextField tfTen;
    private JTextField tfCCCD;
    private JTextField tfPhone;

    public ThemKhachHangDialog(Window owner, CustomerService customerService, Runnable onSuccess) {
        super(owner, "Thêm khách hàng", ModalityType.APPLICATION_MODAL);
        this.customerService = customerService;
        this.onSuccess = onSuccess;
        initUI();
        pack();
        setSize(420, 240);
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(8,8));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8,8,8,8);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0; gc.gridy = 0;
        form.add(new JLabel("Họ tên:"), gc);
        gc.gridx = 1;
        tfTen = new JTextField(22);
        form.add(tfTen, gc);

        gc.gridx = 0; gc.gridy++;
        form.add(new JLabel("CCCD:"), gc);
        gc.gridx = 1;
        tfCCCD = new JTextField(22);
        form.add(tfCCCD, gc);

        gc.gridx = 0; gc.gridy++;
        form.add(new JLabel("Điện thoại:"), gc);
        gc.gridx = 1;
        tfPhone = new JTextField(22);
        form.add(tfPhone, gc);

        add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Hủy");
        JButton btnAdd = new JButton("Thêm");
        bottom.add(btnCancel);
        bottom.add(btnAdd);
        add(bottom, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dispose());

        btnAdd.addActionListener(e -> {
            String ten = tfTen.getText().trim();
            String cccd = tfCCCD.getText().trim();
            String phone = tfPhone.getText().trim();

            // Validate name
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng", "Lỗi", JOptionPane.WARNING_MESSAGE);
                tfTen.requestFocusInWindow();
                return;
            }

            if (cccd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số căn cước (12 chữ số, bắt đầu bằng 0 hoặc 1)", "Lỗi", JOptionPane.WARNING_MESSAGE);
                tfCCCD.requestFocusInWindow();
                return;
            }
            if (!cccd.matches("^[01]\\d{11}$")) {
                JOptionPane.showMessageDialog(this, "CCCD không hợp lệ. CCCD phải gồm đúng 12 chữ số và bắt đầu bằng 0 hoặc 1.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                tfCCCD.requestFocusInWindow();
                return;
            }

            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại (10 chữ số)", "Lỗi", JOptionPane.WARNING_MESSAGE);
                tfPhone.requestFocusInWindow();
                return;
            }
            if (!phone.matches("^(09|03|05|02|07)\\d{8}$")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ. Phải có 10 chữ số và bắt đầu bằng một trong các đầu: 09, 03, 05, 02, 07.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                tfPhone.requestFocusInWindow();
                return;
            }

            // Tạo entity khách hàng tạm để truyền vào service
            KhachHang kh = new KhachHang();
            kh.setTenKhachHang(ten);
            kh.setCCCD(cccd.isEmpty() ? null : cccd);
            kh.setSoDienThoai(phone.isEmpty() ? null : phone);

            try {
                KhachHang created = customerService.createCustomerV2(kh);
                if (created == null) {
                    JOptionPane.showMessageDialog(this, "Không thể thêm khách hàng (có thể trùng tên)", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công: " + created.getMaKhachHang(), "Thành công", JOptionPane.INFORMATION_MESSAGE);

                if (onSuccess != null) onSuccess.run();
                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
