package vn.iuh.gui.panel;


import vn.iuh.constraint.EntityIDSymbol;
import vn.iuh.dao.NhanVienDAO;
import vn.iuh.dao.PhienDangNhapDAO;
import vn.iuh.dao.TaiKhoanDAO;
import vn.iuh.dto.event.create.LoginEvent;
import vn.iuh.entity.NhanVien;
import vn.iuh.entity.PhienDangNhap;
import vn.iuh.entity.TaiKhoan;
import vn.iuh.gui.base.Main;
import vn.iuh.service.AccountService;
import vn.iuh.service.impl.AccountServiceImpl;
import vn.iuh.util.EntityUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.Timestamp;

public class LoginPanelV2 extends JPanel implements ActionListener {
    private JButton btnLogin;
    private JLabel lblForgotPassword;
    private PlaceholderTextField txtUser;
    private PlaceholderPassword txtPass;

    private JButton btnConfirmReset;
    private JButton btnCancelReset;
    private PlaceholderTextField txtFullName, txtPhone, txtCardID, txtUsername;

    private final PhienDangNhapDAO phienDangNhapDao;
    private final TaiKhoanDAO taiKhoanDAO;
    private final NhanVienDAO nhanVienDAO;
    private final Main mainInstance;

    private JPanel mainFormContainer;
    private JPanel pnlLogin;
    private JPanel pnlForgot;
    private final int FORM_WIDTH = 430;
    private final int FORM_HEIGHT = 500;

    public LoginPanelV2(Main mainInstance) {
        this.mainInstance = mainInstance;
        this.phienDangNhapDao = new PhienDangNhapDAO();
        this.taiKhoanDAO = new TaiKhoanDAO();
        this.nhanVienDAO = new NhanVienDAO();
        setLayout(new GridBagLayout());

        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setPreferredSize(new Dimension(950, 550));

        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));
        JLabel lblHotelName = new JLabel("Hai Quân Đức Thịnh", SwingConstants.CENTER);
        lblHotelName.setFont(new Font("Serif", Font.BOLD, 36));
        lblHotelName.setForeground(new Color(50, 50, 50));
        topPanel.add(lblHotelName);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 40, 50));

        JPanel leftPanel = new LeftImagePanel();
        leftPanel.setBackground(Color.WHITE);

        JPanel rightPanel = new JPanel(null); // Layout null để setBounds thủ công khi trượt
        rightPanel.setBackground(Color.WHITE);

        mainFormContainer = new JPanel(null);
        mainFormContainer.setBackground(Color.WHITE);
        mainFormContainer.setBounds(0, 0, FORM_WIDTH, FORM_HEIGHT * 2);

        createLoginPanel();
        createForgotPasswordPanel();

        pnlLogin.setBounds(0, 0, FORM_WIDTH, FORM_HEIGHT);
        pnlForgot.setBounds(0, FORM_HEIGHT, FORM_WIDTH, FORM_HEIGHT);

        mainFormContainer.add(pnlLogin);
        mainFormContainer.add(pnlForgot);
        rightPanel.add(mainFormContainer);

        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);

        cardPanel.add(topPanel, BorderLayout.NORTH);
        cardPanel.add(centerPanel, BorderLayout.CENTER);

        add(cardPanel);
    }

    private void createLoginPanel() {
        pnlLogin = new JPanel();
        pnlLogin.setLayout(new BoxLayout(pnlLogin, BoxLayout.Y_AXIS));
        pnlLogin.setBackground(Color.WHITE);
        pnlLogin.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel lblTitle = new JLabel("THÔNG TIN ĐĂNG NHẬP");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(50, 50, 50));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlLogin.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlLogin.add(lblTitle);
        pnlLogin.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel userPanel = createInputPanel("\u2709", txtUser = new PlaceholderTextField("Username"));
        pnlLogin.add(userPanel);
        pnlLogin.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel passPanel = createInputPanel("\uD83D\uDD12", txtPass = new PlaceholderPassword("Password"));
        pnlLogin.add(passPanel);
        pnlLogin.add(Box.createRigidArea(new Dimension(0, 30)));

        btnLogin = new RoundedButton("LOGIN");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(350, 45));
        btnLogin.setPreferredSize(new Dimension(350, 45));
        btnLogin.setBackground(new Color(102, 204, 102));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.addActionListener(this);
        // Hover effect
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btnLogin.setBackground(new Color(82, 184, 82)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btnLogin.setBackground(new Color(102, 204, 102)); }
        });
        pnlLogin.add(btnLogin);
        pnlLogin.add(Box.createRigidArea(new Dimension(0, 20)));

        lblForgotPassword = new JLabel("Forgot Password ?");
        lblForgotPassword.setFont(new Font("Arial", Font.ITALIC, 13));
        lblForgotPassword.setForeground(new Color(75, 101, 224));
        lblForgotPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblForgotPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // SỰ KIỆN CLICK: TRƯỢT LÊN
        lblForgotPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                animateSlide(true);
            }
        });
        pnlLogin.add(lblForgotPassword);
    }

    private void createForgotPasswordPanel() {
        pnlForgot = new JPanel();
        pnlForgot.setLayout(new BoxLayout(pnlForgot, BoxLayout.Y_AXIS));
        pnlForgot.setBackground(Color.WHITE);
        pnlForgot.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JLabel lblTitle = new JLabel("KHÔI PHỤC MẬT KHẨU");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(220, 53, 69));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlForgot.add(lblTitle);
        pnlForgot.add(Box.createRigidArea(new Dimension(0, 20)));

        txtFullName = new PlaceholderTextField("Họ tên");
        txtPhone = new PlaceholderTextField("Số điện thoại");
        txtCardID = new PlaceholderTextField("CCCD/CMND");
        txtUsername = new PlaceholderTextField("Tên đăng nhập");

        pnlForgot.add(createSimpleInput(txtFullName));
        pnlForgot.add(Box.createRigidArea(new Dimension(0, 25)));
        pnlForgot.add(createSimpleInput(txtPhone));
        pnlForgot.add(Box.createRigidArea(new Dimension(0, 25)));
        pnlForgot.add(createSimpleInput(txtCardID));
        pnlForgot.add(Box.createRigidArea(new Dimension(0, 25)));
        pnlForgot.add(createSimpleInput(txtUsername));
        pnlForgot.add(Box.createRigidArea(new Dimension(0, 40)));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setMaximumSize(new Dimension(350, 45));

        btnCancelReset = new RoundedButton("Hủy");
        btnCancelReset.setBackground(new Color(150, 150, 150));
        btnCancelReset.setForeground(Color.WHITE);
        btnCancelReset.setPreferredSize(new Dimension(100, 40));

        btnCancelReset.addActionListener(e -> animateSlide(false));

        btnConfirmReset = new RoundedButton("Xác nhận");
        btnConfirmReset.setBackground(new Color(220, 53, 69));
        btnConfirmReset.setForeground(Color.WHITE);
        btnConfirmReset.setPreferredSize(new Dimension(100, 40));
        btnConfirmReset.addActionListener(this);

        btnPanel.add(btnCancelReset);
        btnPanel.add(btnConfirmReset);
        pnlForgot.add(btnPanel);
    }

    private JPanel createInputPanel(String iconText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        panel.setMaximumSize(new Dimension(350, 45));
        JLabel icon = new JLabel(iconText);
        icon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
        icon.setForeground(new Color(150, 150, 150));
        ((JComponent)field).setBorder(BorderFactory.createEmptyBorder());
        ((JComponent)field).setBackground(new Color(240, 240, 240));
        ((JTextField)field).setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(icon, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSimpleInput(JTextField field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(7, 15, 7, 15));
        panel.setMaximumSize(new Dimension(350, 35));
        field.setBorder(BorderFactory.createEmptyBorder());
        field.setBackground(new Color(245, 245, 245));
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    // --- Animation Logic (Slide Effect) ---
    private Timer slideTimer;
    private void animateSlide(boolean showForgot) {
        if (slideTimer != null && slideTimer.isRunning()) return;

        int finalYLogin = showForgot ? -FORM_HEIGHT : 0;
        int finalYForgot = showForgot ? 0 : FORM_HEIGHT;
        int step = 25;
        int delay = 10;

        slideTimer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Rectangle boundsLogin = pnlLogin.getBounds();
                Rectangle boundsForgot = pnlForgot.getBounds();
                boolean finished = false;

                if (showForgot) {
                    if (boundsLogin.y > finalYLogin) {
                        pnlLogin.setBounds(0, boundsLogin.y - step, FORM_WIDTH, FORM_HEIGHT);
                        pnlForgot.setBounds(0, boundsForgot.y - step, FORM_WIDTH, FORM_HEIGHT);
                    } else finished = true;
                } else {
                    if (boundsLogin.y < finalYLogin) {
                        pnlLogin.setBounds(0, boundsLogin.y + step, FORM_WIDTH, FORM_HEIGHT);
                        pnlForgot.setBounds(0, boundsForgot.y + step, FORM_WIDTH, FORM_HEIGHT);
                    } else finished = true;
                }

                if (finished) {
                    pnlLogin.setBounds(0, finalYLogin, FORM_WIDTH, FORM_HEIGHT);
                    pnlForgot.setBounds(0, finalYForgot, FORM_WIDTH, FORM_HEIGHT);
                    slideTimer.stop();
                    repaint();
                }
            }
        });
        slideTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnLogin)) {
            String username = txtUser.getText().trim();
            String password = new String(txtPass.getPassword());
            if (username.isEmpty() || password.isEmpty() || password.equals("Password")) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LoginEvent loginEvent = new LoginEvent(username, password);
            AccountService accountService = new AccountServiceImpl();
            if (accountService.handleLogin(loginEvent)) {
                PhienDangNhap phienDangNhapMoiNhat = phienDangNhapDao.timPhienDangNhapMoiNhat();
                String maPhienDangNhapMoiNhat = phienDangNhapMoiNhat != null ? phienDangNhapMoiNhat.getMaPhienDangNhap() : "PN00000000";
                String newMaPhienDangNhap = EntityUtil.increaseEntityID(maPhienDangNhapMoiNhat, EntityIDSymbol.LOGIN_SESSION.getPrefix(), EntityIDSymbol.LOGIN_SESSION.getLength());
                TaiKhoan tk = taiKhoanDAO.timTaiKhoanBangUserName(username);
                PhienDangNhap phienDangNhap = new PhienDangNhap();
                phienDangNhap.setMaPhienDangNhap(newMaPhienDangNhap);
                phienDangNhap.setSoQuay(1);
                phienDangNhap.setTgBatDau(new Timestamp(System.currentTimeMillis()));
                phienDangNhap.setMaTaiKhoan(tk.getMaTaiKhoan());
                phienDangNhapDao.themPhienDangNhap(phienDangNhap);
                Main.setCurrenLoginSession(newMaPhienDangNhap);
                mainInstance.refreshSidebar();
                Main.showRootCard("MainUI");
            } else {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng", "Thất bại", JOptionPane.ERROR_MESSAGE);
            }
        } else if (o.equals(btnConfirmReset)) {
            String fullName = txtFullName.getText().trim();
            String phone = txtPhone.getText().trim();
            String cccd = txtCardID.getText().trim();
            String username = txtUsername.getText().trim();

            if (fullName.isEmpty() || fullName.equals("Họ tên") ||
                    phone.isEmpty() || phone.equals("Số điện thoại") ||
                    cccd.isEmpty() || cccd.equals("CCCD/CMND") ||
                    username.isEmpty() || username.equals("Tên đăng nhập")) {


                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập đầy đủ thông tin xác thực!",
                        "Thiếu thông tin",
                        JOptionPane.WARNING_MESSAGE);

                return;
            }

            NhanVien nv = nhanVienDAO.timNhanVienBangCCCD(cccd);
            TaiKhoan tk = taiKhoanDAO.timTaiKhoanBangUserName(username);

            if (taiKhoanDAO.xacThucThongTin(nv, tk)) {
                boolean updatePassword = taiKhoanDAO.doiMatKhau(tk.getMaNhanVien(), "1");

                if (updatePassword) {
                    JOptionPane.showMessageDialog(this,
                            "Xác thực thành công!\nMật khẩu đã được đặt lại về mặc định: 1",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Trượt ẩn form quên mật khẩu đi
                    animateSlide(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi hệ thống, không thể đổi mật khẩu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Thông tin xác thực không chính xác.\nVui lòng kiểm tra lại Họ tên, SĐT, CCCD và Username.",
                        "Sai thông tin",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, new Color(147, 112, 219), w, h, new Color(75, 101, 224));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }

    static class LeftImagePanel extends JPanel {
        private Image userImage;

        public LeftImagePanel() {
            setLayout(new GridBagLayout());
            setBackground(Color.WHITE);

            try {
                java.net.URL imgURL = getClass().getResource("/icons/abc.png");
                if (imgURL != null) {
                    ImageIcon icon = new ImageIcon(imgURL);
                    userImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                }
            } catch (Exception e) {
                // Ignore error, will draw default icon
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            g2d.setColor(new Color(245, 245, 245));
            g2d.fill(new Ellipse2D.Double(centerX - 130, centerY - 130, 260, 260));

            drawDecorativeCircle(g2d, centerX - 180, centerY - 50, 15, new Color(100, 200, 255));
            drawDecorativeCircle(g2d, centerX + 165, centerY + 20, 12, new Color(100, 200, 255));
            drawDecorativeCircle(g2d, centerX - 160, centerY + 100, 10, new Color(200, 220, 100));
            drawDecorativeCircle(g2d, centerX + 150, centerY - 80, 8, new Color(150, 200, 150));

            g2d.setColor(new Color(70, 90, 110));
            g2d.fill(new RoundRectangle2D.Double(centerX - 80, centerY - 60, 160, 120, 15, 15));

            if (userImage != null) {
                g2d.drawImage(userImage, centerX - 40, centerY - 40, 80, 80, null);
            } else {
                g2d.setColor(new Color(180, 190, 200));
                g2d.fill(new Ellipse2D.Double(centerX - 20, centerY - 30, 40, 40));
                g2d.fill(new RoundRectangle2D.Double(centerX - 30, centerY + 15, 60, 35, 20, 20));
            }
        }

        private void drawDecorativeCircle(Graphics2D g2d, int x, int y, int size, Color color) {
            g2d.setColor(color);
            g2d.fill(new Ellipse2D.Double(x, y, size, size));
        }
    }

    static class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Arial", Font.BOLD, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isPressed()) g2.setColor(getBackground().darker());
            else g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class PlaceholderTextField extends JTextField {
        private final String placeholder;
        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
            setForeground(Color.GRAY);
            setText(placeholder);
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (getText().equals(placeholder)) { setText(""); setForeground(new Color(50, 50, 50)); }
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) { setText(placeholder); setForeground(Color.GRAY); }
                }
            });
        }
        @Override
        public void setText(String t) {
            super.setText(t);
            if(!t.equals(placeholder)) setForeground(new Color(50, 50, 50));
        }
    }

    static class PlaceholderPassword extends JPasswordField {
        private final String placeholder;
        public PlaceholderPassword(String placeholder) {
            this.placeholder = placeholder;
            setForeground(Color.GRAY);
            setText(placeholder);
            setEchoChar((char)0);
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (String.valueOf(getPassword()).equals(placeholder)) { setText(""); setForeground(new Color(50, 50, 50)); setEchoChar('●'); }
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (getPassword().length == 0) { setText(placeholder); setForeground(Color.GRAY); setEchoChar((char) 0); }
                }
            });
        }
    }

//    public static void main(String[] args) {
//        try { UIManager.setLookAndFeel(new FlatMacLightLaf()); } catch (UnsupportedLookAndFeelException e) { e.printStackTrace(); }
//        SwingUtilities.invokeLater(() -> { Main mainFrame = new Main(); mainFrame.setVisible(true); });
//    }

    public JButton getBtnLogin() {
        return btnLogin;
    }
}