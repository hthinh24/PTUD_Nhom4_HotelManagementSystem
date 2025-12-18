package vn.iuh.dto.response;

import java.sql.Timestamp;

public class CustomerInfoWithPayments {
    private final String customerId;
    private final String CCCD;
    private final String customerName;
    private final String customerPhone;
    private final Timestamp timeIn;
    private final Timestamp timeOut;
    private final String customerNote;
    private final double totalServiceCost;
    private final double totalDepositPayment;

//    public CustomerInfoWithPayments(String customerId, String CCCD, String customerName, String customerPhone,
//                                    String customerNote, double totalServiceCost, double totalDepositPayment) {
//        this.customerId = customerId;
//        this.CCCD = CCCD;
//        this.customerName = customerName;
//        this.customerPhone = customerPhone;
//        this.customerNote = customerNote;
//        this.totalServiceCost = totalServiceCost;
//        this.totalDepositPayment = totalDepositPayment;
//    }

    public CustomerInfoWithPayments(String customerId, String CCCD, String customerName, String customerPhone,
                                    Timestamp timeIn, Timestamp timeOut, String customerNote, double totalServiceCost,
                                    double totalDepositPayment) {
        this.customerId = customerId;
        this.CCCD = CCCD;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.customerNote = customerNote;
        this.totalServiceCost = totalServiceCost;
        this.totalDepositPayment = totalDepositPayment;
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

    public String getCustomerNote() {
        return customerNote;
    }

    public Timestamp getTimeIn() {
        return timeIn;
    }

    public Timestamp getTimeOut() {
        return timeOut;
    }

    public double getTotalServiceCost() {
        return totalServiceCost;
    }

    public double getTotalDepositPayment() {
        return totalDepositPayment;
    }

    @Override
    public String toString() {
        return "CustomerInfo{" +
               "customerId='" + customerId + '\'' +
               ", CCCD='" + CCCD + '\'' +
               ", customerName='" + customerName + '\'' +
               ", customerPhone='" + customerPhone + '\'' +
               '}';
    }
}
