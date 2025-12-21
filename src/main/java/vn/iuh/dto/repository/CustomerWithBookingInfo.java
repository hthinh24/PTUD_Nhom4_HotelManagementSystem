package vn.iuh.dto.repository;

import java.sql.Timestamp;

public class CustomerWithBookingInfo {
    private final String customerId;
    private final String CCCD;
    private final String customerName;
    private final String customerPhone;
    private final Timestamp timeIn;
    private final Timestamp timeOut;
    private final String customerNote;

    public CustomerWithBookingInfo(String customerId, String CCCD, String customerName, String customerPhone,
                                   Timestamp timeIn, Timestamp timeOut, String customerNote) {
        this.customerId = customerId;
        this.CCCD = CCCD;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.customerNote = customerNote;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCCCD() {
        return CCCD;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public Timestamp getTimeIn() {
        return timeIn;
    }

    public Timestamp getTimeOut() {
        return timeOut;
    }

    public String getCustomerNote() {
        return customerNote;
    }
}
