package com.aaseya.MDM.dto;

public class CustomerResponseDTO {

    private GeneralDetails generalDetails;
    private FinancialDetails financialDetails;
    private ContactDetails contactDetails;
    private AdditionalDetails additionalDetails;

    // Getters and Setters
    public GeneralDetails getGeneralDetails() {
        return generalDetails;
    }

    public void setGeneralDetails(GeneralDetails generalDetails) {
        this.generalDetails = generalDetails;
    }

    public FinancialDetails getFinancialDetails() {
        return financialDetails;
    }

    public void setFinancialDetails(FinancialDetails financialDetails) {
        this.financialDetails = financialDetails;
    }

    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(ContactDetails contactDetails) {
        this.contactDetails = contactDetails;
    }

    public AdditionalDetails getAdditionalDetails() {
        return additionalDetails;
    }

    public void setAdditionalDetails(AdditionalDetails additionalDetails) {
        this.additionalDetails = additionalDetails;
    }

    // Inner Classes for Structured Response

    public static class GeneralDetails {
        private Long customerId;
        private String customerName;
        private String customerType;
        private String customerGroup;
        private String territory;
        private String marketSegment;
        private String industry;
        private String website;
        private String customerAddress;

        // Getters and Setters
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        
        public String getCustomerType() { return customerType; }
        public void setCustomerType(String customerType) { this.customerType = customerType; }
        
        public String getCustomerGroup() { return customerGroup; }
        public void setCustomerGroup(String customerGroup) { this.customerGroup = customerGroup; }
        
        public String getTerritory() { return territory; }
        public void setTerritory(String territory) { this.territory = territory; }
        
        public String getMarketSegment() { return marketSegment; }
        public void setMarketSegment(String marketSegment) { this.marketSegment = marketSegment; }
        
        public String getIndustry() { return industry; }
        public void setIndustry(String industry) { this.industry = industry; }
        
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }
        
        public String getCustomerAddress() { return customerAddress; }
        public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    }

    public static class FinancialDetails {
        private String defaultCurrency;
        private String defaultPriceList;
        private String gstn;
        private String panNumber;
        private String creditScore;

        // Getters and Setters
        public String getDefaultCurrency() { return defaultCurrency; }
        public void setDefaultCurrency(String defaultCurrency) { this.defaultCurrency = defaultCurrency; }
        
        public String getDefaultPriceList() { return defaultPriceList; }
        public void setDefaultPriceList(String defaultPriceList) { this.defaultPriceList = defaultPriceList; }
        
        public String getGstn() { return gstn; }
        public void setGstn(String gstn) { this.gstn = gstn; }
        
        public String getPanNumber() { return panNumber; }
        public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
        
        public String getCreditScore() { return creditScore; }
        public void setCreditScore(String creditScore) { this.creditScore = creditScore; }
    }

    public static class ContactDetails {
        private String phoneNumber;
        private String emailId;
        private String accountManager;
        private String primaryAddress;

        // Getters and Setters
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getEmailId() { return emailId; }
        public void setEmailId(String emailId) { this.emailId = emailId; }
        
        public String getAccountManager() { return accountManager; }
        public void setAccountManager(String accountManager) { this.accountManager = accountManager; }
        
        public String getPrimaryAddress() { return primaryAddress; }
        public void setPrimaryAddress(String primaryAddress) { this.primaryAddress = primaryAddress; }
    }

    public static class AdditionalDetails {
        private String isInternalCustomer;
        private String modifiedBy;

        // Getters and Setters
        public String getIsInternalCustomer() { return isInternalCustomer; }
        public void setIsInternalCustomer(String isInternalCustomer) { this.isInternalCustomer = isInternalCustomer; }
        
        public String getModifiedBy() { return modifiedBy; }
        public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }
    }
}
