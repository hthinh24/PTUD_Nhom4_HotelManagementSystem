package vn.iuh.util;

import vn.iuh.gui.panel.booking.*;

public class RefreshManager {
    private static BookingManagementPanel bookingManagementPanel;
    private static ReservationManagementPanel reservationManagementPanel;
    private static PreReservationSearchPanel preReservationSearchPanel;
    private static RoomUsageFormPanel currentRoomUsageFormPanel;

    // Registration methods
    public static void setBookingManagementPanel(BookingManagementPanel panel) {
        bookingManagementPanel = panel;
        System.out.println("RefreshManager: setBookingManagementPanel registered");
    }

    public static void setReservationManagementPanel(ReservationManagementPanel panel) {
        reservationManagementPanel = panel;
        System.out.println("RefreshManager: ReservationManagementPanel registered");
    }

    public static void setPreReservationManagementPanel(PreReservationManagementPanel panel) {
        System.out.println("RefreshManager: PreReservationManagementPanel registered");
    }

    public static void setPreReservationSearchPanel(PreReservationSearchPanel panel) {
        preReservationSearchPanel = panel;
        System.out.println("RefreshManager: PreReservationSearchPanel registered");
    }

    public static void setCurrentRoomUsageFormPanel(RoomUsageFormPanel panel) {
        currentRoomUsageFormPanel = panel;
        System.out.println("RefreshManager: CurrentRoomUsageFormPanel registered");
    }

    // Individual refresh methods
    public static void refreshBookingManagementPanel() {
        if (bookingManagementPanel != null)
            bookingManagementPanel.refreshPanel();
    }

    public static void refreshReservationManagementPanel() {
        if (reservationManagementPanel != null)
            reservationManagementPanel.refreshPanel();
    }

    public static void refreshPreReservationSearchPanel() {
        if (preReservationSearchPanel != null)
            preReservationSearchPanel.refreshPanel();
    }

    public static void refreshCurrentRoomUsageFormPanel() {
        if (currentRoomUsageFormPanel != null)
            currentRoomUsageFormPanel.refreshPanel();
    }

    // Comprehensive refresh method
    public static void refreshAll() {
        System.out.println("RefreshManager: Refreshing all panels...");
        refreshBookingManagementPanel();
        refreshReservationManagementPanel();
        refreshPreReservationSearchPanel();
        refreshCurrentRoomUsageFormPanel();
    }

    // Method specifically for after booking operations
    public static void refreshAfterBooking() {
        System.out.println("RefreshManager: Refreshing after booking operation...");
        refreshBookingManagementPanel();
        refreshReservationManagementPanel();
        refreshPreReservationSearchPanel();
        refreshCurrentRoomUsageFormPanel();
    }

    public static void refreshAfterCancelReservation() {
        System.out.println("RefreshManager: Refreshing after cancel reservation...");
        refreshBookingManagementPanel();
        refreshReservationManagementPanel();
        refreshPreReservationSearchPanel();
        refreshCurrentRoomUsageFormPanel();
    }

    public static void refreshAfterCheckIn() {
        System.out.println("RefreshManager: Refreshing after CheckIn operation...");
        refreshBookingManagementPanel();
        refreshReservationManagementPanel();
        refreshPreReservationSearchPanel();
        refreshCurrentRoomUsageFormPanel();
    }

    public static void refreshAfterTransfer() {
        System.out.println("RefreshManager: Refreshing after transfer room operation...");
        refreshBookingManagementPanel();
        refreshReservationManagementPanel();
        refreshPreReservationSearchPanel();
        refreshCurrentRoomUsageFormPanel();
    }

    public static void refreshAfterCheckout() {
        System.out.println("RefreshManager: Refreshing after Checkout operation...");
        refreshBookingManagementPanel();
        refreshReservationManagementPanel();
        refreshPreReservationSearchPanel();
        refreshCurrentRoomUsageFormPanel();
    }

    public static void refreshAfterCleaning() {
        System.out.println("RefreshManager: Refreshing after cleaning operation...");
        refreshBookingManagementPanel();
    }
}