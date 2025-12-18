package vn.iuh.gui.panel.booking;

import com.formdev.flatlaf.FlatClientProperties;
import vn.iuh.dto.response.PreReservationResponse;
import vn.iuh.gui.base.CustomUI;
import vn.iuh.gui.base.Main;
import vn.iuh.service.BookingService;
import vn.iuh.service.CheckinService;
import vn.iuh.service.impl.BookingServiceImpl;
import vn.iuh.service.impl.CheckinServiceImpl;
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

public class PreReservationSearchPanel extends JPanel {
    private String parentPanelName;
    private String roomName;
    private String roomId;

    private final BookingService bookingService;
    private final CheckinService checkinService;

    // Filter components
    private JTextField txtRoomName;
    private JTextField txtCustomerName;
    private JSpinner spnCheckinDate;
    private JButton btnReset;
    private JButton btnClose;

    // Table components
    private JTable reservationTable;
    private DefaultTableModel tableModel;

    // Data
    private List<PreReservationResponse> allReservations;
    private List<PreReservationResponse> filteredReservations;

    // Filter state
    private ReservationFilter reservationFilter;

    public PreReservationSearchPanel(String parentPanelName, String roomName, String roomId) {
        this.parentPanelName = parentPanelName;
        this.roomName = roomName;
        this.roomId = roomId;

        // Initialize services and data
        bookingService = new BookingServiceImpl();
        checkinService = new CheckinServiceImpl();
        reservationFilter = new ReservationFilter(null, null, null);
        RefreshManager.setPreReservationSearchPanel(this);

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
        JLabel lblTop = new JLabel("ĐƠN ĐẶT TRƯỚC TẠI PHÒNG " + roomName.toUpperCase(), SwingConstants.CENTER);
        lblTop.setForeground(CustomUI.white);
        lblTop.setFont(CustomUI.bigFont);

        pnlTop.setBackground(CustomUI.blue);

        pnlTop.setPreferredSize(new Dimension(0, CustomUI.TOP_PANEL_HEIGHT));
        pnlTop.setMinimumSize(new Dimension(0, CustomUI.TOP_PANEL_HEIGHT));
        pnlTop.setMaximumSize(new Dimension(Integer.MAX_VALUE, CustomUI.TOP_PANEL_HEIGHT));
        pnlTop.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        ImageIcon undoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/undo.png")));
        undoIcon = new ImageIcon(undoIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));

        btnClose = new JButton("x");
        btnClose.setFont(CustomUI.bigFont);
        btnClose.setBackground(CustomUI.red);
        btnClose.setForeground(CustomUI.white);
        btnClose.setPreferredSize(new Dimension(50, 20));
        btnClose.setFocusPainted(false);
        btnClose.putClientProperty(FlatClientProperties.STYLE, " arc: 10");
        btnClose.addActionListener(e -> {
            Main.showCard(parentPanelName);
        });

        pnlTop.add(lblTop, BorderLayout.CENTER);
        pnlTop.add(btnClose, BorderLayout.EAST);

        add(pnlTop, BorderLayout.NORTH);
    }

    private void createFilterPanel() {
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CustomUI.gray, 2),
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
        filterPanel.setPreferredSize(new Dimension(0, 150));
        filterPanel.setMinimumSize(new Dimension(0, 150));
        filterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        wrapper.add(filterPanel, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
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
        String[] columnNames = {"Khách hàng", "Đơn đặt phòng", "Phòng", "Checkin", "Checkout"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        // Create table
        reservationTable = new JTable(tableModel) { // Tạo JTable mới dựa trên model
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                // prepareRenderer được gọi mỗi khi JTable vẽ 1 cell.
                Component c = super.prepareRenderer(renderer, row, column);

                // reuse font constant (không new font mỗi cell)
                c.setFont(CustomUI.TABLE_FONT);

                if (!isRowSelected(row)) {
                    // reuse color constant
                    c.setBackground(row % 2 == 0 ? CustomUI.ROW_ODD : CustomUI.ROW_EVEN);
                } else {
                    c.setBackground(CustomUI.ROW_SELECTED_COLOR);
                }
                return c;
            }
        };
        reservationTable.setFont(CustomUI.TABLE_FONT); // Non-bold font for data
        reservationTable.setRowHeight(40);
        reservationTable.setSelectionBackground(CustomUI.ROW_SELECTED_COLOR);
        reservationTable.setGridColor(CustomUI.tableBorder);
        reservationTable.setShowGrid(true); // Show grid lines
        reservationTable.setIntercellSpacing(new Dimension(1, 1)); // Thin borders

        // Enhanced header styling
        reservationTable.getTableHeader().setPreferredSize(new Dimension(reservationTable.getWidth(), 40));
        reservationTable.getTableHeader().setFont(CustomUI.HEADER_FONT);
        reservationTable.getTableHeader().setBackground(CustomUI.TABLE_HEADER_BACKGROUND);
        reservationTable.getTableHeader().setForeground(CustomUI.TABLE_HEADER_FOREGROUND);
        reservationTable.getTableHeader().setOpaque(true);
        reservationTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, CustomUI.tableBorder));

        // Set alternating row colors
        reservationTable.setDefaultRenderer(Object.class, new AlternatingRowRenderer());

        // Set column widths using relative proportions
        reservationTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        reservationTable.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int tableWidth = reservationTable.getWidth();
                TableColumnModel columnModel = reservationTable.getColumnModel();

                columnModel.getColumn(0).setPreferredWidth((int) (tableWidth * 0.20)); // 15%
                columnModel.getColumn(1).setPreferredWidth((int) (tableWidth * 0.20)); // 15%
                columnModel.getColumn(2).setPreferredWidth((int) (tableWidth * 0.20)); // 10%
                columnModel.getColumn(3).setPreferredWidth((int) (tableWidth * 0.20)); // 15%
                columnModel.getColumn(4).setPreferredWidth((int) (tableWidth * 0.20)); // 15%
            }
        });

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(CustomUI.gray, 2));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add wrapper for padding top & buttom
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        wrapper.add(scrollPane, BorderLayout.CENTER);

        add(wrapper, BorderLayout.SOUTH);

        // Populate table with initial data
        populateTable();
    }

    // Custom renderer for alternating row colors and proper styling
    private class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Set font to non-bold for data
            component.setFont(CustomUI.TABLE_FONT);

            if (isSelected) {
                component.setBackground(CustomUI.ROW_SELECTED_COLOR);
                component.setForeground(Color.BLACK);
            } else {
                // Alternating row colors
                if (row % 2 == 0) {
                    component.setBackground(CustomUI.ROW_EVEN);
                } else {
                    component.setBackground(CustomUI.ROW_ODD);
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

        for (PreReservationResponse reservation : allReservations) {
            if (passesAllFilters(reservation)) {
                filteredReservations.add(reservation);
            }
        }

        populateTable();
    }

    private boolean passesAllFilters(PreReservationResponse reservation) {
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
        for (PreReservationResponse reservation : filteredReservations) {
            Object[] rowData = new Object[5];
            rowData[0] = reservation.getCustomerName();
            rowData[1] = reservation.getMaDonDatPhong();
            rowData[2] = reservation.getRoomName();
            rowData[3] = reservation.getTimeIn() != null ? dateFormat.format(reservation.getTimeIn()) : "N/A";
            rowData[4] = reservation.getTimeOut() != null ? dateFormat.format(reservation.getTimeOut()) : "N/A";

            tableModel.addRow(rowData);
        }
    }

    public void refreshPanel() {
        loadReservationData();
        resetFilters();
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

