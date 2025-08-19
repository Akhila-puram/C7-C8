package com.aaseya.MDM.dto;

public class CustomerDetailsDTO {

    private String customerId;
    private String customerName;
    private String customerType;
    private String risk;
    private String reviewStatus;
    private String status;

    // Single constructor to handle all cases (null values allowed)
    public CustomerDetailsDTO(String customerId, String customerName, String customerType, String risk, String reviewStatus, String status) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerType = customerType;
        this.risk = risk;
        this.reviewStatus = reviewStatus;
        this.status = status;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
