package vn.iuh.gui.panel.booking;

import com.formdev.flatlaf.FlatClientProperties;
import vn.iuh.dto.response.ReservationFormResponse;
import vn.iuh.gui.base.CustomUI;
import vn.iuh.gui.base.Main;
import vn.iuh.service.BookingService;
import vn.iuh.service.impl.BookingServiceImpl;
import vn.iuh.util.RefreshManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ReservationFormSearchPanel extends JPanel {
    private String parentPanelName;
    private String roomName;
    private String roomId;

    private final BookingService bookingService;

    // Filter components
    private JTextField txtRoomName;
    private JTextField txtCustomerName;
    private JSpinner spnCheckinDate;
    private JButton btnReset;
    private JButton btnUndo;

    // Table components
    private JTable reservationTable;
    private DefaultTableModel tableModel;

    // Data
    private List<ReservationFormResponse> allReservations;
    private List<ReservationFormResponse> filteredReservations;

    // Filter state
    private ReservationFilter reservationFilter;

    public ReservationFormSearchPanel(String parentPanelName, String roomName, String roomId) {
        this.parentPanelName = parentPanelName;
        this.roomName = roomName;
        this.roomId = roomId;

        // Initialize services and data
        bookingService = new BookingServiceImpl();
        reservationFilter = new ReservationFilter(null, null, null);
        RefreshManager.setReservationFormSearchPanel(this);

        // Load data
        loadReservationData();
        
        setLayout(new BorderLayout());
        init();
    }
    
    private void loadReservationData() {
        allReservations = bookingService.getReseravtionFormByRoomId(roomId);
        filteredReservations = new ArrayList<>(allReservations);
    }
    
    private void init() {
        createTopPanel();
        createFilterPanel();
        createTablePanel();
    }
    
    private void createTopPanel() {
        JPanel pnlTop = new JPanel(new BorderLayout());
        JLabel lblTop = new JLabel("Quản lí đơn đặt phòng", SwingConstants.CENTER);
        lblTop.setForeground(CustomUI.white);
        lblTop.setFont(CustomUI.bigFont);
        
        pnlTop.setBackground(CustomUI.blue);
        
        pnlTop.setPreferredSize(new Dimension(0, 50));
        pnlTop.setMinimumSize(new Dimension(0, 50));
        pnlTop.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pnlTop.putClientProperty(FlatClientProperties.STYLE, " arc: 10");

        ImageIcon undoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/undo.png")));
        undoIcon = new ImageIcon(undoIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));

        btnUndo = new JButton();
        btnUndo.setBackground(CustomUI.red);
        btnUndo.setIcon(undoIcon);
        btnUndo.setForeground(Color.WHITE);
        btnUndo.setFont(CustomUI.normalFont);
        btnUndo.setPreferredSize(new Dimension(60, 40));
        btnUndo.setFocusPainted(false);
        btnUndo.putClientProperty(FlatClientProperties.STYLE, " arc: 10");
        btnUndo.addActionListener(e -> {
            Main.showCard(parentPanelName);
        });

        pnlTop.add(btnUndo, BorderLayout.WEST);
        pnlTop.add(lblTop, BorderLayout.CENTER);
        
        add(pnlTop, BorderLayout.NORTH);
    }
    
    private void createFilterPanel() {
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CustomUI.lightBlue, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Initialize filter components
        initializeFilterComponents();
        
        // Row 1: Tên phòng và Tên khách hàng
        addFilterRow(filterPanel, gbc, 0, 0, "Tên phòng:", txtRoomName);
        addFilterRow(filterPanel, gbc, 0, 2, "Tên khách hàng:", txtCustomerName);
        
        // Row 2: Thời gian checkin và Reset button
        addFilterRow(filterPanel, gbc, 1, 0, "Thời gian checkin:", spnCheckinDate);
        
        // Reset button
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        btnReset.setPreferredSize(new Dimension(120, 35));
        filterPanel.add(btnReset, gbc);
        
        // Set fixed height for filter panel
        filterPanel.setPreferredSize(new Dimension(0, 120));
        filterPanel.setMinimumSize(new Dimension(0, 120));
        filterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        add(filterPanel, BorderLayout.CENTER);
    }
    
    private void initializeFilterComponents() {
        // Room name text field with auto-filtering
        txtRoomName = new JTextField(15);
        txtRoomName.setFont(CustomUI.smallFont);
        txtRoomName.setText(roomName);
        reservationFilter.roomName = roomName;
        txtRoomName.setEditable(false); // Make it read-only if initialized with a room name

        // Customer name text field with auto-filtering
        txtCustomerName = new JTextField(15);
        txtCustomerName.setFont(CustomUI.smallFont);
        txtCustomerName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilters(); // Auto-filter on every key release
            }
        });

        // Check-in date spinner with time
        spnCheckinDate = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnCheckinDate, "dd/MM/yyyy HH:mm");
        spnCheckinDate.setEditor(dateEditor);
        spnCheckinDate.setValue(new Date());
        spnCheckinDate.setFont(CustomUI.smallFont);
        spnCheckinDate.addChangeListener(e -> applyFilters());
        
        // Reset button
        btnReset = new JButton("HOÀN TÁC");
        btnReset.setFont(CustomUI.smallFont);
        btnReset.setBackground(CustomUI.lightGray);
        btnReset.setForeground(Color.BLACK);
        btnReset.setFocusPainted(false);
        btnReset.putClientProperty(FlatClientProperties.STYLE, " arc: 10");
        btnReset.addActionListener(e -> resetFilters());
    }
    
    private void createTablePanel() {
        // Create table model
        String[] columnNames = {"Khách hàng", "Đơn đặt phòng", "Phòng", "Checkin", "Checkout", "Thao tác"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only action column is editable
            }
        };
        
        // Create table
        reservationTable = new JTable(tableModel);
        reservationTable.setFont(CustomUI.tableDataFont); // Non-bold font for data
        reservationTable.setRowHeight(40);
        reservationTable.setSelectionBackground(CustomUI.tableSelection);
        reservationTable.setGridColor(CustomUI.tableBorder);
        reservationTable.setShowGrid(true); // Show grid lines
        reservationTable.setIntercellSpacing(new Dimension(1, 1)); // Thin borders

        // Enhanced header styling
        reservationTable.getTableHeader().setFont(CustomUI.tableHeaderFont);
        reservationTable.getTableHeader().setBackground(CustomUI.tableHeaderBackground);
        reservationTable.getTableHeader().setForeground(CustomUI.tableHeaderForeground);
        reservationTable.getTableHeader().setOpaque(true);
        reservationTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, CustomUI.tableBorder));

        // Set alternating row colors
        reservationTable.setDefaultRenderer(Object.class, new AlternatingRowRenderer());

        // Set column widths using relative proportions
        TableColumnModel columnModel = reservationTable.getColumnModel();
        reservationTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        columnModel.getColumn(0).setPreferredWidth(150); // Khách hàng - 15%
        columnModel.getColumn(1).setPreferredWidth(150); // Đơn đặt phòng - 10%
        columnModel.getColumn(2).setPreferredWidth(100); // Phòng - 10%
        columnModel.getColumn(3).setPreferredWidth(150); // Checkin - 15%
        columnModel.getColumn(4).setPreferredWidth(150); // Checkout - 15%
        columnModel.getColumn(5).setPreferredWidth(300); // Thao tác - 30%

        // Set cell renderer for action column
        reservationTable.getColumn("Thao tác").setCellRenderer(new ActionButtonRenderer());
        reservationTable.getColumn("Thao tác").setCellEditor(new ActionButtonEditor());
        
        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.SOUTH);

        // Populate table with initial data
        populateTable();
    }

    // Custom renderer for alternating row colors and proper styling
    private class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Set font to non-bold for data
            component.setFont(CustomUI.tableDataFont);

            if (isSelected) {
                component.setBackground(CustomUI.tableSelection);
                component.setForeground(Color.BLACK);
            } else {
                // Alternating row colors
                if (row % 2 == 0) {
                    component.setBackground(CustomUI.tableRowEven);
                } else {
                    component.setBackground(CustomUI.tableRowOdd);
                }
                component.setForeground(Color.BLACK);
            }

            // Center align text for all columns except action column
            if (column < 4) {
                setHorizontalAlignment(JLabel.CENTER);
            }

            // Add subtle border
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, CustomUI.tableBorder));

            return component;
        }
    }

    private void addFilterRow(JPanel panel, GridBagConstraints gbc, int row, int startCol, String labelText, JComponent component) {
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        
        // Label
        gbc.gridx = startCol;
        JLabel label = new JLabel(labelText);
        label.setFont(CustomUI.smallFont);
        panel.add(label, gbc);
        
        // Component
        gbc.gridx = startCol + 1;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        component.setFont(CustomUI.smallFont);
        component.setPreferredSize(new Dimension(180, 35));
        component.setMinimumSize(new Dimension(150, 35));
        panel.add(component, gbc);
    }
    
    private void applyFilters() {
        reservationFilter.roomName = txtRoomName.getText().trim();
        reservationFilter.customerName = txtCustomerName.getText().trim();
        reservationFilter.checkinDate = (Date) spnCheckinDate.getValue();
        
        filteredReservations.clear();
        
        for (ReservationFormResponse reservation : allReservations) {
            if (passesAllFilters(reservation)) {
                filteredReservations.add(reservation);
            }
        }
        
        populateTable();
    }
    
    private boolean passesAllFilters(ReservationFormResponse reservation) {
        // Room name filter
        if (reservationFilter.roomName != null && !reservationFilter.roomName.isEmpty()) {
            if (!reservation.getRoomName().toLowerCase().contains(reservationFilter.roomName.toLowerCase())) {
                return false;
            }
        }
        
        // Customer name filter
        if (reservationFilter.customerName != null && !reservationFilter.customerName.isEmpty()) {
            if (!reservation.getCustomerName().toLowerCase().contains(reservationFilter.customerName.toLowerCase())) {
                return false;
            }
        }
        
        // Checkin date filter - show all reservations with check-in time after the selected date/time
        if (reservationFilter.checkinDate != null && reservation.getTimeIn() != null) {
            Date filterDateTime = reservationFilter.checkinDate;
            Date reservationDateTime = new Date(reservation.getTimeIn().getTime());

            // Show reservations where check-in is after the filter date/time
            if (reservationDateTime.before(filterDateTime)) {
                return false;
            }
        }
        
        return true;
    }
    
    private void resetFilters() {
        txtCustomerName.setText("");
        spnCheckinDate.setValue(new Date());
        
        reservationFilter = new ReservationFilter(null, null, null);
        filteredReservations = new ArrayList<>(allReservations);
        populateTable();
    }
    
    private void populateTable() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // Sort filtered reservations by check-in date (nearest first)
        filteredReservations.sort((r1, r2) -> {
            if (r1.getTimeIn() == null && r2.getTimeIn() == null) return 0;
            if (r1.getTimeIn() == null) return 1;
            if (r2.getTimeIn() == null) return -1;
            return r1.getTimeIn().compareTo(r2.getTimeIn());
        });

        // Add filtered reservations to table
        for (ReservationFormResponse reservation : filteredReservations) {
            Object[] rowData = new Object[6];
            rowData[0] = reservation.getCustomerName();
            rowData[1] = reservation.getMaDonDatPhong();
            rowData[2] = reservation.getRoomName();
            rowData[3] = reservation.getTimeIn() != null ? dateFormat.format(reservation.getTimeIn()) : "N/A";
            rowData[4] = reservation.getTimeOut() != null ? dateFormat.format(reservation.getTimeOut()) : "N/A";
            rowData[5] = reservation; // Store the reservation object for action buttons

            tableModel.addRow(rowData);
        }
    }
    
    private void handleCheckIn(ReservationFormResponse reservation) {
        int result = JOptionPane.showConfirmDialog(this,
            "Xác nhận check-in cho khách " + reservation.getCustomerName() + " vào phòng " + reservation.getRoomName() + "?",
            "Xác nhận check-in", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                "Đã check-in thành công cho khách " + reservation.getCustomerName() + " vào phòng " + reservation.getRoomName(),
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // TODO: Implement actual check-in logic
            // bookingService.checkInReservation(reservation.getId());

            RefreshManager.refreshAfterCancelReservation();
        }
    }

    private void handleChangeRoom(ReservationFormResponse reservation) {
        String newRoom = JOptionPane.showInputDialog(this,
            "Nhập số phòng muốn chuyển đến:",
            "Đổi phòng", JOptionPane.QUESTION_MESSAGE);
        
        if (newRoom != null && !newRoom.trim().isEmpty()) {
            int result = JOptionPane.showConfirmDialog(this,
                "Xác nhận đổi phòng từ " + reservation.getRoomName() + " sang " + newRoom + "?",
                "Xác nhận đổi phòng", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this,
                    "Đã đổi phòng thành công từ " + reservation.getRoomName() + " sang " + newRoom,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // TODO: Implement actual room change logic
                // bookingService.changeRoom(reservation.getId(), newRoom);
                
                RefreshManager.refreshAfterCancelReservation();
            }
        }
    }
    
    private void handleCancelReservation(ReservationFormResponse reservation) {
        int result = JOptionPane.showConfirmDialog(this,
            "Xác nhận hủy đơn đặt phòng " + reservation.getMaDonDatPhong() + " của khách " + reservation.getCustomerName() + "?",
            "Hủy đơn đặt phòng", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            System.out.println("Cancelling reservation ID: " + reservation.getMaDonDatPhong());
            boolean isSuccess = bookingService.cancelReservation(reservation.getMaDonDatPhong());
            if (!isSuccess) {
                JOptionPane.showMessageDialog(this,
                    "Hủy đơn đặt phòng thất bại. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                "Đã hủy đơn đặt phòng " + reservation.getRoomName() + " thành công",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

            RefreshManager.refreshAfterCancelReservation();
        }
    }

    public void refreshPanel() {
        loadReservationData();
        resetFilters();
    }
    
    // Custom cell renderer for action buttons
    private class ActionButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton btnCheckIn;
        private JButton btnChangeRoom;
        private JButton btnCancel;
        
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
            setOpaque(true);
            
            // Check-in button
            btnCheckIn = new JButton("Check-in");
            btnCheckIn.setFont(CustomUI.verySmallFont);
            btnCheckIn.setBackground(CustomUI.darkGreen);
            btnCheckIn.setForeground(Color.WHITE);
            btnCheckIn.setPreferredSize(new Dimension(120, 30));
            btnCheckIn.setFocusPainted(false);
            btnCheckIn.putClientProperty(FlatClientProperties.STYLE, " arc: 8");

            // Change room button
            btnChangeRoom = new JButton("Đổi phòng");
            btnChangeRoom.setFont(CustomUI.verySmallFont);
            btnChangeRoom.setBackground(CustomUI.lightBlue);
            btnChangeRoom.setForeground(Color.WHITE);
            btnChangeRoom.setPreferredSize(new Dimension(120, 30));
            btnChangeRoom.setFocusPainted(false);
            btnChangeRoom.putClientProperty(FlatClientProperties.STYLE, " arc: 8");
            
            // Cancel button (small square with trash icon)
            ImageIcon trashIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/bin.png")));
            trashIcon = new ImageIcon(trashIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
            btnCancel = new JButton(trashIcon);
            btnCancel.setBackground(CustomUI.red);
            btnCancel.setForeground(Color.WHITE);
            btnCancel.setPreferredSize(new Dimension(30, 30));
            btnCancel.setFocusPainted(false);
            btnCancel.putClientProperty(FlatClientProperties.STYLE, " arc: 8");
            btnCancel.setToolTipText("Hủy đơn");

            add(btnCheckIn);
            add(btnChangeRoom);
            add(btnCancel);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }
    
    // Custom cell editor for action buttons
    private class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btnCheckIn;
        private JButton btnChangeRoom;
        private JButton btnCancel;
        private ReservationFormResponse currentReservation;
        private int currentRow;

        public ActionButtonEditor() {
            super(new JCheckBox());
            
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));

            // Check-in button
            btnCheckIn = new JButton("Check-in");
            btnCheckIn.setFont(CustomUI.verySmallFont);
            btnCheckIn.setBackground(CustomUI.darkGreen);
            btnCheckIn.setForeground(Color.WHITE);
            btnCheckIn.setPreferredSize(new Dimension(120, 30));
            btnCheckIn.setFocusPainted(false);
            btnCheckIn.putClientProperty(FlatClientProperties.STYLE, " arc: 8");
            btnCheckIn.addActionListener(e -> {
                ReservationFormResponse freshReservation = (ReservationFormResponse) tableModel.getValueAt(currentRow, 5);
                handleCheckIn(freshReservation);
                fireEditingStopped();
            });

            // Change room button
            btnChangeRoom = new JButton("Đổi phòng");
            btnChangeRoom.setFont(CustomUI.verySmallFont);
            btnChangeRoom.setBackground(CustomUI.lightBlue);
            btnChangeRoom.setForeground(Color.WHITE);
            btnChangeRoom.setPreferredSize(new Dimension(120, 30));
            btnChangeRoom.setFocusPainted(false);
            btnChangeRoom.putClientProperty(FlatClientProperties.STYLE, " arc: 8");
            btnChangeRoom.addActionListener(e -> {
                ReservationFormResponse freshReservation = (ReservationFormResponse) tableModel.getValueAt(currentRow, 5);
                handleChangeRoom(freshReservation);
                fireEditingStopped();
            });
            
            // Cancel button (small square with trash icon)
            ImageIcon trashIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/error.png")));
            trashIcon = new ImageIcon(trashIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
            btnCancel = new JButton(trashIcon);
            btnCancel.setBackground(CustomUI.red);
            btnCancel.setForeground(Color.WHITE);
            btnCancel.setPreferredSize(new Dimension(30, 30));
            btnCancel.setFocusPainted(false);
            btnCancel.putClientProperty(FlatClientProperties.STYLE, " arc: 8");
            btnCancel.setToolTipText("Hủy đơn");
            btnCancel.addActionListener(e -> {
                ReservationFormResponse freshReservation = (ReservationFormResponse) tableModel.getValueAt(currentRow, 5);
                handleCancelReservation(freshReservation);
            });
            
            panel.add(btnCheckIn);
            panel.add(btnChangeRoom);
            panel.add(btnCancel);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentReservation = (ReservationFormResponse) value;
            currentRow = row;
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return currentReservation;
        }
    }
    
    // Filter state holder
    private static class ReservationFilter {
        String roomName;
        String customerName;
        Date checkinDate;
        
        public ReservationFilter(String roomName, String customerName, Date checkinDate) {
            this.roomName = roomName;
            this.customerName = customerName;
            this.checkinDate = checkinDate;
        }
    }
}

