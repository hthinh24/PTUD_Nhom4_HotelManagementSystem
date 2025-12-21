package vn.iuh.gui.panel;

import com.formdev.flatlaf.FlatClientProperties;
import vn.iuh.gui.base.CustomUI;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TroGiupPanel extends JPanel {
    private JTree treeMenu;
    private JPanel pnlContent;
    private Map<String, GuideData> guideDataMap;

    public TroGiupPanel() {
        setLayout(new BorderLayout());
        createTopPanel();
        initData();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Hệ thống trợ giúp");

        // Nhóm 1: Quản lý đặt phòng
        DefaultMutableTreeNode nodeBooking = createCategoryNode("Quản lý Đặt phòng");
        nodeBooking.add(new DefaultMutableTreeNode("Đặt phòng"));
        root.add(nodeBooking);

        // Nhóm 2: Phòng
        DefaultMutableTreeNode nodeRm = createCategoryNode("Quản lý phòng");
        nodeRm.add(new DefaultMutableTreeNode("Thêm phòng"));
        nodeRm.add(new DefaultMutableTreeNode("Sửa phòng"));
        nodeRm.add(new DefaultMutableTreeNode("Xóa phòng"));
        root.add(nodeRm);

        // Nhóm 3: Loại Phòng
        DefaultMutableTreeNode nodeRt = createCategoryNode("Quản lý loại phòng");
        nodeRt.add(new DefaultMutableTreeNode("Thêm loại phòng"));
        nodeRt.add(new DefaultMutableTreeNode("Sửa loại phòng"));
        nodeRt.add(new DefaultMutableTreeNode("Xóa loại phòng"));
        root.add(nodeRt);

        // Nhóm 4: Dịch vụ
        DefaultMutableTreeNode nodeSv = createCategoryNode("Quản lý dịch vụ");
        nodeSv.add(new DefaultMutableTreeNode("Thêm dịch vụ"));
        nodeSv.add(new DefaultMutableTreeNode("Sửa dịch vụ"));
        nodeSv.add(new DefaultMutableTreeNode("Xóa dịch vụ"));
        nodeSv.add(new DefaultMutableTreeNode("Chỉnh tồn kho"));
        root.add(nodeSv);

        // Nhóm 5: Loại dịch vụ
        DefaultMutableTreeNode nodeSt = createCategoryNode("Quản lý loại dịch vụ");
        nodeSt.add(new DefaultMutableTreeNode("Thêm loại dịch vụ"));
        nodeSt.add(new DefaultMutableTreeNode("Sửa loại dịch vụ"));
        nodeSt.add(new DefaultMutableTreeNode("Xóa loại dịch vụ"));
        root.add(nodeSt);

        // Nhóm 6: Hóa đơn
        DefaultMutableTreeNode noteIV = createCategoryNode("Hóa đơn");
        noteIV.add(new DefaultMutableTreeNode("Tìm hóa đơn"));
        root.add(noteIV);

        // Nhóm 7: Quản lý Nhân viên
        DefaultMutableTreeNode nodeEmp = createCategoryNode("Quản lý nhân viên");
        nodeEmp.add(new DefaultMutableTreeNode("Thêm nhân viên"));
        nodeEmp.add(new DefaultMutableTreeNode("Sửa nhân viên"));
        nodeEmp.add(new DefaultMutableTreeNode("Xóa nhân viên"));
        nodeEmp.add(new DefaultMutableTreeNode("Phân quyền tài khoản"));
        root.add(nodeEmp);

        //Nhóm 8: Quản lý tài khoản
        DefaultMutableTreeNode nodeAcc = createCategoryNode("Quản Lý Tài Khoản");
        nodeAcc.add(new DefaultMutableTreeNode("Thêm tài khoản"));
        nodeAcc.add(new DefaultMutableTreeNode("Sửa tài khoản"));
        root.add(nodeAcc);

        // Nhóm 9: Khách hàng
        DefaultMutableTreeNode noteCu = createCategoryNode("Quản lý khách hàng");
        noteCu.add(new DefaultMutableTreeNode("Thêm khách hàng"));
        noteCu.add(new DefaultMutableTreeNode("Sửa khách hàng"));
        noteCu.add(new DefaultMutableTreeNode("Xóa khách hàng"));
        root.add(noteCu);

        // Nhóm 10: Quản lý phụ phí
        DefaultMutableTreeNode nodeFee = createCategoryNode("Quản lý phụ phí");
        nodeFee.add(new DefaultMutableTreeNode("Sửa phụ phí"));
        root.add(nodeFee);

        // Nhóm 11: Thống kê
        DefaultMutableTreeNode nodeTK = createCategoryNode("Thống kê");
        nodeTK.add(new DefaultMutableTreeNode("Thống kê hiệu suất"));
        nodeTK.add(new DefaultMutableTreeNode("Thống kê doanh thu"));
        root.add(nodeTK);

        // Nhóm 12: Hệ thống
        DefaultMutableTreeNode nodeSys = createCategoryNode("Hệ thống");
        nodeSys.add(new DefaultMutableTreeNode("Quên mật khẩu"));
        nodeSys.add(new DefaultMutableTreeNode("Đổi mật khẩu"));
        nodeSys.add(new DefaultMutableTreeNode("Thiết lập hệ thống"));
        root.add(nodeSys);

        treeMenu = new JTree(new DefaultTreeModel(root));
        styleTree(treeMenu);

        treeMenu.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeMenu.getLastSelectedPathComponent();
            if (node == null || !node.isLeaf()) return;
            loadGuideContent(node.toString());
        });

        JScrollPane scrollTree = new JScrollPane(treeMenu);
        scrollTree.setPreferredSize(new Dimension(280, 0));
        scrollTree.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));

        pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(Color.WHITE);
        pnlContent.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JScrollPane scrollContent = new JScrollPane(pnlContent);
        scrollContent.setBorder(null);
        scrollContent.getVerticalScrollBar().setUnitIncrement(20);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTree, scrollContent);
        splitPane.setDividerLocation(280);
        splitPane.setDividerSize(3);
        splitPane.setResizeWeight(0.0);

        add(splitPane, BorderLayout.CENTER);
        loadWelcome();

        // Mở rộng tất cả các nhánh cây khi khởi động
//        for (int i = 0; i < treeMenu.getRowCount(); i++) {
//            treeMenu.expandRow(i);
//        }
    }

    // Helper tạo Node có style
    private DefaultMutableTreeNode createCategoryNode(String title) {
        return new DefaultMutableTreeNode(title);
    }

    // Helper style cho Tree
    private void styleTree(JTree tree) {
        tree.setRowHeight(35);
        tree.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tree.setBackground(new Color(245, 247, 250)); // Màu nền menu nhẹ
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
        renderer.setBackgroundNonSelectionColor(new Color(245, 247, 250));
        renderer.setTextSelectionColor(Color.BLACK);
        renderer.setBackgroundSelectionColor(new Color(200, 220, 240)); // Màu khi chọn
        renderer.setBorderSelectionColor(new Color(200, 220, 240));
    }

    private void loadWelcome() {
        pnlContent.removeAll();

        JLabel lblIcon = new JLabel("💡");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Trung tâm trợ giúp");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(50, 50, 50));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea txtIntro = new JTextArea(
                "Chào mừng bạn đến với  hướng dẫn sử dụng phần mềm Quản lý khách sạn Hai Quân Đức Thịnh.\n" +
                        "Vui lòng chọn một chức năng ở danh sách bên trái để xem hướng dẫn chi tiết từng bước."
        );
        txtIntro.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtIntro.setForeground(Color.GRAY);
        txtIntro.setEditable(false);
        txtIntro.setLineWrap(true);
        txtIntro.setWrapStyleWord(true);
        txtIntro.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtIntro.setMaximumSize(new Dimension(600, 100));
        txtIntro.setOpaque(false);

        pnlContent.add(Box.createVerticalGlue());
        pnlContent.add(lblIcon);
        pnlContent.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlContent.add(lblTitle);
        pnlContent.add(Box.createRigidArea(new Dimension(0, 20)));
        pnlContent.add(txtIntro);
        pnlContent.add(Box.createVerticalGlue());
        pnlContent.revalidate();
        pnlContent.repaint();
    }

    private void loadGuideContent(String key) {
        pnlContent.removeAll();

        GuideData data = guideDataMap.get(key);
        if (data == null) {
            JLabel lbl = new JLabel("Đang cập nhật nội dung cho: " + key);
            lbl.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            pnlContent.add(lbl);
            pnlContent.revalidate();
            pnlContent.repaint();
            return;
        }

        // 1. Tiêu đề bài hướng dẫn
        JLabel lblHeader = new JLabel(data.title);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblHeader.setForeground(new Color(0, 102, 204));
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlContent.add(lblHeader);

        // Đường kẻ ngang
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(2000, 10));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlContent.add(Box.createRigidArea(new Dimension(0, 15)));
        pnlContent.add(sep);
        pnlContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Render từng bước
        for (GuideStep step : data.steps) {
            JPanel pnlStep = new JPanel();
            pnlStep.setLayout(new BoxLayout(pnlStep, BoxLayout.Y_AXIS));
            pnlStep.setBackground(Color.WHITE);
            pnlStep.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Text hướng dẫn
            JLabel lblStepText = new JLabel("<html><body style='width: 500px'>" + step.text + "</body></html>");
            lblStepText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            lblStepText.setForeground(new Color(30, 30, 30));
            lblStepText.setAlignmentX(Component.LEFT_ALIGNMENT);
            pnlStep.add(lblStepText);

            // Ảnh minh họa
            pnlStep.add(Box.createRigidArea(new Dimension(0, 10)));
            JLabel lblImage = new JLabel();
            lblImage.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Logic load ảnh (hoặc vẽ khung giả nếu chưa có ảnh)
            ImageIcon icon = loadResizedIcon(step.imagePath, 600, 350);
            if (icon != null) {
                lblImage.setIcon(icon);
                lblImage.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            } else {
                lblImage.setText("<html><div style='width:500px; height:200px; border:1px dashed #ccc; background:#f9f9f9; text-align:center; line-height:200px; color:#999;'>Ảnh minh họa: " + step.imagePath + "</div></html>");
            }
            pnlStep.add(lblImage);

            pnlStep.add(Box.createRigidArea(new Dimension(0, 30))); // Khoảng cách giữa các bước
            pnlContent.add(pnlStep);
        }

        pnlContent.revalidate();
        pnlContent.repaint();
    }

    // --- DATA STRUCTURES ---

    private void initData() {
        guideDataMap = new HashMap<>();

        // Quản lý đặt phòng
        GuideData d21 = new GuideData("Quy trình đặt phòng");
        d21.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý đặt phòng</i> từ thanh bên trái.", "/images/Muc/MucQuanLyPhong.png");
        d21.addStep("<b>Bước 2:</b> Trong bảng quản lý đặt phòng, chọn danh sách phòng đang còn trống muốn đặt hoặc sử dụng bộ lọc phía trên góc trái.", "/images/GiaoDien/GDChinh.png");
        d21.addStep("<b>Bước 3:</b> Chọn phòng, điển thông tin cá nhân của khách hàng.", "/images/GiaoDien/GDDatPhong.png");
        d21.addStep("<b>Bước 4:</b> Gọi dịch vụ các loại và số lượng của chúng.", "/images/GiaoDien/GDGoiDichVu.png");
        d21.addStep("<b>Bước 5:</b> Nhấn nút <b>Lưu lại</b> để hoàn tất.", "");
        guideDataMap.put("Đặt phòng", d21);

        // Quản lys phòng d3
        GuideData d31 = new GuideData("Quy trình thêm phòng mới");
        d31.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý phòng</i> từ thanh bên trái.", "/images/Muc/MucQuanLyPhong.png");
        d31.addStep("<b>Bước 2:</b> Trong bảng quản lý phòng, chọn nút <b>Thêm phòng</b> (Màu xanh lá).", "/images/GiaoDien/GDQuanLyPhong.png");
        d31.addStep("<b>Bước 3:</b> Nhập tên phòng, loại phòng, nội thất, mô tả, ghi chú (nếu có).", "/images/Form/FormThemPhong.png");
        d31.addStep("<b>Bước 4:</b> Nhấn nút <b>Lưu lại</b> để hoàn tất.", "");
        guideDataMap.put("Thêm phòng", d31);

        GuideData d32 = new GuideData("Quy trình sửa phòng");
        d32.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý phòng</i> từ thanh bên trái.", "/images/Muc/MucQuanLyPhong.png");
        d32.addStep("<b>Bước 2:</b> Trong bảng quản lý phòng, chọn nút <b>Sửa phòng</b> (Màu xanh dương).", "/images/GiaoDien/GDQuanLyPhong.png");
        d32.addStep("<b>Bước 3:</b> Chọn một phòng ở bảng danh sách các phòng để chỉnh sửa", "");
        d32.addStep("<b>Bước 4:</b> Chỉnh sửa loại phòng, mô tả, ghi chú (nếu có).", "/images/Form/FormSuaPhong.png");
        d32.addStep("<b>Bước 5:</b> Nhấn nút <b>Lưu</b> để hoàn tất.", "");
        guideDataMap.put("Sửa phòng", d32);

        GuideData d33 = new GuideData("Quy trình xóa phòng");
        d33.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý phòng</i> từ thanh bên trái.", "/images/Muc/MucQuanLyPhong.png");
        d33.addStep("<b>Bước 2:</b> Trong bảng quản lý phòng, chọn nút <b>Xóa phòng</b> (Màu xanh đỏ).", "/images/GiaoDien/GDQuanLyPhong.png");
        d33.addStep("<b>Bước 3:</b> Chọn một phòng ở bảng danh sách các phòng để xóa", "");
        d33.addStep("<b>Bước 4:</b> Nếu phòng đang bảo trì thì chưa được xóa.", "/images/ThongBao/TBKhongDuocXoaPhong.png");
        d33.addStep("<b>Bước 5:</b> Nhấn nút <b>Xác nhận</b> để hoàn tất", "/images/ThongBao/TBXacNhanXoaPhong.png");
        guideDataMap.put("Xóa phòng", d33);

        // Quản lý loại phòng d4
        GuideData d41 = new GuideData("Quy trình thêm loại phòng mới");
        d41.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý loại phòng</i> từ thanh bên trái.", "/images/Muc/MucQuanLyPhong.png");
        d41.addStep("<b>Bước 2:</b> Trong bảng quản lý loại phòng, chọn nút <b>Thêm loại phòng</b> (Màu xanh lá).", "/images/GiaoDien/GDQuanLyLoaiPhong.png");
        d41.addStep("<b>Bước 3:</b> Nhập tên loại phòng, số người tối đa, phân loại ,giá, thêm danh sách nội thất có sẵn vào loại phòng.", "/images/Form/FormThemLoaiPhong.png");
        d41.addStep("<b>Bước 4:</b> Nhấn nút <b>Lưu </b> để hoàn tất.", "");
        guideDataMap.put("Thêm loại phòng", d41);

        GuideData d42 = new GuideData("Quy trình sửa loại phòng");
        d42.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý loại phòng</i> từ thanh bên trái.", "/images/Muc/MucQuanLyPhong.png");
        d42.addStep("<b>Bước 2:</b> Trong bảng quản lý phòng, chọn nút <b>Sửa loại phòng</b> (Màu xanh dương).", "/images/GiaoDien/GDQuanLyLoaiPhong.png");
        d42.addStep("<b>Bước 3:</b> Chọn một loại phòng ở bảng danh sách các loại phòng để chỉnh sửa", "");
        d42.addStep("<b>Bước 4:</b> Chỉnh sửa tên loại phòng, số người tối đa, phân loại, giá, bổ sung nội thất có sẵn. Nhấn lưu(nằm góc phải bên dưới)", "/images/Form/FormSuaLoaiPhong.png");
        d42.addStep("<b>Bước 5:</b> Xác nhận <b>Ok</b> để hoàn tất.", "/images/ThongBao/TBThanhCong.png");
        guideDataMap.put("Sửa loại phòng", d42);

        GuideData d43 = new GuideData("Quy trình xóa loại phòng");
        d43.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý loại phòng</i> từ thanh bên trái.", "/images/Muc/MucQuanLyPhong.png");
        d43.addStep("<b>Bước 2:</b> Trong bảng quản lý phòng, chọn nút <b>Xóa loại phòng</b> (Màu đỏ).", "/images/GiaoDien/GDQuanLyLoaiPhong.png");
        d43.addStep("<b>Bước 3:</b> Chọn một loại phòng ở bảng danh sách các phòng bên dưới để xóa", "");
        d43.addStep("<b>Bước 4:</b> Nếu loại phòng đang có phòng thuộc loại này thì chưa thể xóa.", "/images/ThongBao/TBKhongDuocLoaiPhong.png");
        d43.addStep("<b>Bước 5:</b> Nếu không, nhấn nút <b>Yes</b> để hoàn tất", "/images/ThongBao/TBXacNhanXoaLoaiPhong.png");
        guideDataMap.put("Xóa loại phòng", d43);

        // Quản lý nhân viên d5
        GuideData d51 = new GuideData("Quy trình thêm mới nhân viên");
        d51.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý nhân viên</i> từ thanh bên trái.", "/images/Muc/MucQuanLyNhanVien.png");
        d51.addStep("<b>Bước 2:</b> Trong bảng quản lý loại phòng, chọn nút <b>Thêm nhân viên</b> (Màu xanh lá).", "/images/GiaoDien/GDQuanLyNhanVien.png");
        d51.addStep("<b>Bước 3:</b> Nhập tên nhân viên, cccd, ngày sinh, số điện thoại.", "/images/Form/Formthongtinnhanvien.png");
        d51.addStep("<b>Bước 4:</b> Nhấn nút <b>Lưu </b> để hoàn tất.", "");
        guideDataMap.put("Thêm nhân viên", d51);

        GuideData d52 = new GuideData("Quy trình sửa nhân viên");
        d52.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý nhân viên</i> từ thanh bên trái.", "/images/Muc/MucQuanLyNhanVien.png");
        d52.addStep("<b>Bước 2:</b> Chọn một nhân viên để sửa trong danh sách nhân viên.", "");
        d52.addStep("<b>Bước 3:</b> Trong bảng nhân viên, chọn nút <b>sửa nhân vien</b> (Màu xanh dương).", "/images/GiaoDien/GDQuanLyNhanVien.png");
        d52.addStep("<b>Bước 4:</b> Nhập ten,số CCCD, ngày sinh, số điện thoại muốn thay đổi.", "/images/Form/Formthongtinnhanvien.png");
        d52.addStep("<b>Bước 5:</b> Nhấn nút <b>Lưu lại</b> để hoàn tất.", "");
        guideDataMap.put("Sửa nhân viên", d52);

        GuideData d53 = new GuideData("Quy trình xóa nhân viên");
        d53.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý nhân viên</i> từ thanh bên trái.", "/images/Muc/MucQuanLyNhanVien.png");
        d53.addStep("<b>Bước 2:</b> Chọn một nhân viên để xóa trong danh sách nhân viên, nút màu đỏ.", "");
        d53.addStep("<b>Buớc 3:</b> Xác nhận nhân viên muốn xóa.", "/images/ThongBao/TBXacNhanXoaNhanVien.png");
        guideDataMap.put("Xóa nhân viên", d53);

        // QUản lý tài khoản d6
        GuideData d61 = new GuideData("Quy trình thêm tài khoản");
        d61.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý tài khoản</i> từ thanh bên trái.", "/images/Muc/MucQuanLyNhanVien.png");
        d61.addStep("<b>Bước 2:</b> Trong bảng tài khoản, chọn nút <b>Thêm tài khoản</b> (Màu xanh lá).", "/images/GiaoDien/GDQuanLyTaiKhoan.png");
        d61.addStep("<b>Bước 3:</b> Trong bảng danh sách nhân viên chọn môt nhân viên để tạo tài khoản", "");
        d61.addStep("<b>Bước 4:</b> Chọn chức vụ cho nhân viên ", "/images/Form/FormThemTaiKhoan.png");
        d61.addStep("<b>Bước 4:</b> Xác nhận và hoàn thành", "");
        guideDataMap.put("Thêm tài khoản", d61);

        GuideData d62 = new GuideData("Quy trình sửa tài khoản");
        d62.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý tài khoản</i> từ thanh bên trái.", "/images/Muc/MucQuanLyNhanVien.png");
        d62.addStep("<b>Bước 2:</b> Trong bảng tài khoản, chọn nút <b>Sửa tài khoản</b> (Màu xanh dương).", "/images/GiaoDien/GDQuanLyTaiKhoan.png");
        d62.addStep("<b>Bước 3:</b> Trong bảng danh sách tài khoản chọn môt tài khoản để sửa", "");
        d62.addStep("<b>Bước 4:</b> Cập nhật các thông tin cho tài khoản", "/images/Form/FormSuaTaiKhoan.png");
        d62.addStep("<b>Bước 5:</b> Xác nhận và hoàn thành", "");
        guideDataMap.put("Sửa tài khoản", d62);

        // Quản lý dịch vụ d7
        GuideData d71 = new GuideData("Quy trình thêm dịch vụ mới");
        d71.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý dịch vụ</i> từ thanh bên trái.", "/images/Muc/MucDichVu.png");
        d71.addStep("<b>Bước 2:</b> Trong bảng quản lý dịch vụ, chọn nút <b>Thêm dịch vụ</b> (Màu xanh lá).", "/images/GiaoDien/GDQuanLyDichVu.png");
        d71.addStep("<b>Bước 3:</b> Nhập tên dịch vụ, tồn kho, loại dịch vụ, giá.", "/images/Form/FormThemDichVu.png");
        d71.addStep("<b>Bước 4:</b> Nhấn nút <b>Thêm</b> để xác nhận.", "/images/ThongBao/TBXacNhanThemDichVu.png");
        guideDataMap.put("Thêm dịch vụ", d71);

        GuideData d72 = new GuideData("Quy trình sửa dịch vụ");
        d72.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý dịch vụ</i> từ thanh bên trái.", "/images/Muc/MucDichVu.png");
        d72.addStep("<b>Bước 2:</b> Trong bảng quản lý dịch vụ, chọn nút <b>Sửa dịch vụ</b> (Màu xanh dương).", "/images/GiaoDien/GDQuanLyDichVu.png");
        d72.addStep("<b>Bước 3:</b> Chọn một dịch vụ ở bảng danh sách các dịch vụ để chỉnh sửa", "");
        d72.addStep("<b>Bước 4:</b> Chỉnh sửa tên dịch vụ, tồn kho, loại dịch vụ, giá.", "/images/Form/FormSuaDichVu.png");
        d72.addStep("<b>Bước 5:</b> Nhấn nút <b>Ok</b> để hoàn tất.", "/images/ThongBao/TBThanhCong.png");
        guideDataMap.put("Sửa dịch vụ", d72);

        GuideData d73 = new GuideData("Quy trình xóa dịch vụ");
        d73.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý dịch vụ</i> từ thanh bên trái.", "/images/Muc/MucDichVu.png");
        d73.addStep("<b>Bước 2:</b> Trong bảng quản lý dịch vụ, chọn nút <b>Xóa dịch vụ</b> (Màu đỏ).", "/images/GiaoDien/GDQuanLyDichVu.png");
        d73.addStep("<b>Bước 3:</b> Chọn một dịch vụ ở bảng danh sách các dịch vụ để xóa", "");
        d73.addStep("<b>Bước 4:</b> Nhấn nút <b>Yes</b> để hoàn tất", "/images/ThongBao/TBXacNhanXoaDichVu.png");
        guideDataMap.put("Xóa dịch vụ", d73);

        GuideData d74 = new GuideData("Quy trình chỉnh tồn kho");
        d74.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý dịch vụ</i> từ thanh bên trái.", "/images/Muc/MucDichVu.png");
        d74.addStep("<b>Bước 2:</b> Trong bảng quản lý dịch vụ, chọn nút <b>Chỉnh tồn kho</b> (Màu vàng).", "/images/GiaoDien/GDQuanLyDichVu.png");
        d74.addStep("<b>Bước 3:</b> Chọn một dịch vụ ở bảng danh sách và điền sô lượng", "/images/Form/FormChinhTonKho.png");
        d74.addStep("<b>Bước 4:</b> Nhấn nút <b>Ok</b> để hoàn tất", "/images/ThongBao/TBThanhCong.png");
        guideDataMap.put("Chỉnh tồn kho", d74);

        // Quản lý loại dich vụ d8
        GuideData d81 = new GuideData("Quy trình thêm loại dịch vụ.");
        d81.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý loại dịch vụ</i> từ thanh bên trái.", "/images/Muc/MucDichVu.png");
        d81.addStep("<b>Bước 2:</b> Trong bảng quản lý loại phòng, chọn nút <b>Thêm loại phòng</b> (Màu xanh lá).", "/images/GiaoDien/GDQuanLyLoaiDV.png");
        d81.addStep("<b>Bước 3:</b> Nhập tên loại dịch vụ.", "/images/Form/FormTHemLoaiDV.png");
        d81.addStep("<b>Bước 4:</b> Nhấn nút <b>Lưu </b> để hoàn tất.", "");
        guideDataMap.put("Thêm loại dịch vụ", d81);

        GuideData d82 = new GuideData("Quy trình sửa loại dịch vụ");
        d82.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý loại dịch vụ</i> từ thanh bên trái.", "/images/Muc/MucDichVu.png");
        d82.addStep("<b>Bước 2:</b> Trong bảng quản lý loại dịch vụ, chọn nút <b>Sửa loại dịch vụ</b> (Màu xanh dương).", "/images/GiaoDien/GDQuanLyLoaiDV.png");
        d82.addStep("<b>Bước 3:</b> Chọn một loại dịch vụ ở bảng danh sách các loại dịch vụ để chỉnh sửa", "");
        d82.addStep("<b>Bước 4:</b> Chỉnh sửa tên loại dịch vụ và xác nhận", "/images/Form/FormSuaLoaiDV.png");
        guideDataMap.put("Sửa loại dịch vụ", d82);

        GuideData d83 = new GuideData("Quy trình xóa loại dịch vụ");
        d83.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý loại dịch vụ</i> từ thanh bên trái.", "/images/Muc/MucDichVu.png");
        d83.addStep("<b>Bước 2:</b> Trong bảng quản lý loại dịch vụ, chọn nút <b>Xóa loại dịch vụ</b> (Màu đỏ).", "/images/GiaoDien/GDQuanLyLoaiDV.png");
        d83.addStep("<b>Bước 3:</b> Chọn một loại dịch vụ ở bảng danh sách bên dưới để xóa", "");
        d83.addStep("<b>Bước 4:</b> Nhấn nút <b>Yes</b> để hoàn tất", "/images/ThongBao/TBXacNhanXoaLoaiDV.png");
        guideDataMap.put("Xóa loại dịch vụ", d83);

        // Quản lý khách hàng d9
        GuideData d91 = new GuideData("Quy trình thêm mới khách hàng");
        d91.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý khách hàng</i> từ thanh bên trái.", "/images/Muc/MucKhachHang.png");
        d91.addStep("<b>Bước 2:</b> Trong bảng quản lý khách hàng, chọn nút <b>Thêm khách hàng</b> (Màu xanh lá).", "/images/GiaoDien/GDQuanLyKhachHang.png");
        d91.addStep("<b>Bước 3:</b> Nhập tên khách hàng, cccd số điện thoại.", "/images/Form/FormThemKH.png");
        d91.addStep("<b>Bước 4:</b> Nhấn nút <b>Thêm </b> để hoàn tất.", "/images/ThongBao/TBThemKH.png");
        guideDataMap.put("Thêm khách hàng", d91);

        GuideData d92 = new GuideData("Quy trình sửa khách hàng");
        d92.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý khách hàng</i> từ thanh bên trái.", "/images/Muc/MucKhachHang.png");
        d92.addStep("<b>Bước 2:</b> Chọn một khách hàng để sửa trong danh sách khách hàng.", "");
        d92.addStep("<b>Bước 3:</b> Trong bảng khách hàng, chọn nút <b>sửa khách hàng</b> (Màu xanh dương).", "/images/GiaoDien/GDQuanLyKhachHang.png");
        d92.addStep("<b>Bước 4:</b> Nhập ten,số CCCD, số điện thoại muốn thay đổi.", "/images/Form/FormSuaNV.png");
        d92.addStep("<b>Bước 5:</b> Nhấn nút <b>Lưu lại</b> để hoàn tất.", "");
        guideDataMap.put("Sửa khách hàng", d92);

        GuideData d93 = new GuideData("Quy trình xóa khách hàng");
        d93.addStep("<b>Buớc 1:</b> Chọn chức năng <i>Quản lý khách hàng</i> từ thanh bên trái.", "/images/Muc/MucKhachHang.png");
        d93.addStep("<b>Bước 2:</b> Chọn một khách hàng để xóa trong danh sách, nút màu đỏ.", "");
        d93.addStep("<b>Buớc 3:</b> Xác nhận khách hàng muốn xóa.", "/images/ThongBao/TBXoaKhachHang.png");
        guideDataMap.put("Xóa khách hàng", d93);

        GuideData d94 = new GuideData("Điều chỉnh phụ phí");
        d94.addStep("<b>Bước 1:</b> Chọn chức năng <i>Quản lý phụ phí</i> từ thanh bên trái.", "/images/Muc/MucHeThong.png");
        d94.addStep("<b>Bước 2:</b> Nhấp hai lần vào phụ phí muốn chỉnh sửa.", "/images/GiaoDien/GDPhuPhi.png");
        d94.addStep("<b>Bước 3:</b> Điều chỉnh giá hiện tại.", "/images/Form/FormPhuPhi.png");
        d94.addStep("<b>Bước 4:</b> Cuối cùng nhấn <b>Lưu</b>.", "");
        guideDataMap.put("Sửa phụ phí", d94);

        GuideData d2 = new GuideData("Hướng dẫn Đổi mật khẩu");
        d2.addStep("<b>Bước 1:</b> Click vào xem chi tiết của bạn ở góc trên cùng bên trái màn hình.", "/images/XemChiTiet.png");
        d2.addStep("<b>Bước 2:</b> Một hộp thoại thông tin hiện ra, nhấn vào nút <b>Đổi mật khẩu</b>.", "/images/GiaoDien/GDThongTinCaNhan.png");
        d2.addStep("<b>Bước 3:</b> Nhập mật khẩu hiện tại và mật khẩu mới (2 lần) rồi nhấn Xác nhận.", "/images/Form/FormDoiMatKhau.png");
        guideDataMap.put("Đổi mật khẩu", d2);

        GuideData d8 = new GuideData("Hướng dẫn quên mật khẩu");
        d8.addStep("<b>Bước 1:</b> Click vào quên mật khẩu ở giao diện đăng nhập.", "/images/GiaoDien/GDDangNhap.png");
        d8.addStep("<b>Bước 2:</b> Điền vào các thông tin để lấy lại mật khẩu ở giao diện đăng nhập.", "/images/GiaoDien/GDKhoiPhucMatKhau.png");
        d8.addStep("<b>Bước 3:</b> Xác nhận và mật khẩu sẽ reset về 1.", "");
        guideDataMap.put("Quên mật khẩu", d8);

        GuideData d9 = new GuideData("Hướng dẫn thống kê hiệu suất");
        d9.addStep("<b>Bước 1:</b> Click vào <i>thống kê hiệu suất</i> ở giao diện chính.", "/images/Muc/MucQuanLyPhong.png");
        d9.addStep("<b>Bước 2:</b> Sử dụng bộ lọc để xem loại phòng được sử dụng nhiều nhất, sô giờ và doanh thu tương ứng.", "/images/GiaoDien/GDThongKeHieuSuat.png");
        d9.addStep("<b>Bước 3:</b> Chọn thư mục để xuất file.", "/images/XuatFilePDF.png");
        d9.addStep("<b>Bước 4:</b> Nhấn xuất file excel.", "");
        guideDataMap.put("Thống kê hiệu suất", d9);

        GuideData d10 = new GuideData("Hướng dẫn thống kê doanh thu");
        d10.addStep("<b>Bước 1:</b> Click vào <i>thống kê doanh thu</i> ở giao diện chính.", "/images/Muc/MucHoaDon.png");
        d10.addStep("<b>Bước 2:</b> Sử dụng bộ lọc lọc các hóa đơn trong khoảng thời gian nhất định.", "/images/GiaoDien/GDThongKeDoanhThu.png");
        d10.addStep("<b>Bước 3:</b> Chọn thư mục để xuất file.", "/images/XuatFilePDF.png");
        d10.addStep("<b>Bước 4:</b> Nhấn xuất file excel.", "");
        guideDataMap.put("Thống kê doanh thu", d10);

        GuideData d11 = new GuideData("Tìm hóa đơn");
        d11.addStep("<b>Bước 1:</b> Click vào <i>tìm hóa đơn</i> ở giao diện chính.", "/images/Muc/MucHoaDon.png");
        d11.addStep("<b>Bước 2:</b> Sử dụng bộ lọc lọc các hóa đơn trong khoảng thời gian nhất định.", "/images/GiaoDien/GDQuanLyHoaDon.png");
        guideDataMap.put("Tìm hóa đơn", d11);
    }

    private ImageIcon loadResizedIcon(String path, int maxWidth, int maxHeight) {
        try {
            // Thử load ảnh từ resources
            URL url = getClass().getResource(path);
            if (url == null) return null;

            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage();

            int w = icon.getIconWidth();
            int h = icon.getIconHeight();

            if (w > maxWidth) {
                h = (h * maxWidth) / w;
                w = maxWidth;
            }

            return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            return null;
        }
    }

    private static class GuideData {
        String title;
        List<GuideStep> steps = new ArrayList<>();
        public GuideData(String title) { this.title = title; }
        public void addStep(String text, String img) { steps.add(new GuideStep(text, img)); }
    }

    // Class lưu từng bước
    private static class GuideStep {
        String text;
        String imagePath;
        public GuideStep(String text, String imagePath) { this.text = text; this.imagePath = imagePath; }
    }

    private void createTopPanel() {
        JPanel pnlTop = new JPanel(new BorderLayout());
        JLabel lblTop = new JLabel("Trợ giúp", SwingConstants.CENTER);
        lblTop.setForeground(CustomUI.white);
        lblTop.setFont(CustomUI.normalFont != null ? CustomUI.normalFont.deriveFont(Font.BOLD, 20f) : new Font("Arial", Font.BOLD, 18));

        pnlTop.setBackground(CustomUI.blue);
        pnlTop.add(lblTop, BorderLayout.CENTER);
        pnlTop.setPreferredSize(new Dimension(0, 50));
        pnlTop.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pnlTop.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        add(pnlTop, BorderLayout.NORTH);
    }
}