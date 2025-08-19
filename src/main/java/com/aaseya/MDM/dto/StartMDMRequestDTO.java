package com.aaseya.MDM.dto;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.stereotype.Component;

@Component
public class StartMDMRequestDTO {
    private String modifiedBy;
    private String customerName;
    private String customerType;
    private String customerGroup;
    private String territory;
    private String leadName;
    private String prospectName;
    private String accountManager;
    private String defaultCurrency;
    private String defaultBankAccount;
    private String defaultPriceList;
    private int isInternalCustomer;
    private String marketSegment;
    private String industry;
    private String website;
    private String language; // Default status
    private String customerDetails;
    private String primaryAddress;
    private long mobileNo;
    private String emailId;
    private String gstn;
    private String pan;
    private int disabled;
    private String risk;
    private String status; // Default status
    private String createdBy;
    private LocalDateTime creationTime;
    private LocalDateTime modificationTime;
    private String reviewStatus;
    
    // Getters and Setters
    
    
    public String getModifiedBy() {
        return modifiedBy;
    }

    public String getRisk() {
		return risk;
	}

	public void setRisk(String risk) {
		this.risk = risk;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
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

    public String getCustomerGroup() {
        return customerGroup;
    }

    public void setCustomerGroup(String customerGroup) {
        this.customerGroup = customerGroup;
    }
    
    public String getTerritory() {
        return territory;
    }
    
    public void setTerritory(String territory) {
        this.territory = territory;
    }
    
    public String getLeadName() {
        return leadName;
    }
    
    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }
    
    public String getProspectName() {
        return prospectName;
    }
    
    public void setProspectName(String prospectName) {
        this.prospectName = prospectName;
    }
    
    public String getAccountManager() {
        return accountManager;
    }
    
    public void setAccountManager(String accountManager) {
        this.accountManager = accountManager;
    }
    
    public String getDefaultCurrency() {
        return defaultCurrency;
    }
    
    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
    
    public String getDefaultBankAccount() {
        return defaultBankAccount;
    }
    
    public void setDefaultBankAccount(String defaultBankAccount) {
        this.defaultBankAccount = defaultBankAccount;
    }
    
    public String getDefaultPriceList() {
        return defaultPriceList;
    }
    
    public void setDefaultPriceList(String defaultPriceList) {
        this.defaultPriceList = defaultPriceList;
    }
    
    public int getIsInternalCustomer() {
        return isInternalCustomer;
    }
    
    public void setIsInternalCustomer(int isInternalCustomer) {
        this.isInternalCustomer = isInternalCustomer;
    }
    
    public String getMarketSegment() {
        return marketSegment;
    }
    
    public void setMarketSegment(String marketSegment) {
        this.marketSegment = marketSegment;
    }
    
    public String getIndustry() {
        return industry;
    }
    
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getCustomerDetails() {
        return customerDetails;
    }
    
    public void setCustomerDetails(String customerDetails) {
        this.customerDetails = customerDetails;
    }
    
    public String getPrimaryAddress() {
        return primaryAddress;
    }
    
    public void setPrimaryAddress(String primaryAddress) {
        this.primaryAddress = primaryAddress;
    }
    
    public long getMobileNo() {
        return mobileNo;
    }
    
    public void setMobileNo(long mobileNo) {
        this.mobileNo = mobileNo;
    }
    
    public String getEmailId() {
        return emailId;
    }
    
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
    
    public String getGstn() {
        return gstn;
    }
    
    public void setGstn(String gstn) {
        this.gstn = gstn;
    }
    
    public String getPan() {
        return pan;
    }
    
    public void setPan(String pan) {
        this.pan = pan;
    }
    
    public int getDisabled() {
        return disabled;
    }
    
    public void setDisabled(int disabled) {
        this.disabled = disabled;
    }
    
    public LocalDateTime getCreationTime() {
        return creationTime;
    }
    
    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }
    
    public LocalDateTime getModificationTime() {
        return modificationTime;
    }
    
    public void setModificationTime(LocalDateTime modificationTime) {
        this.modificationTime = modificationTime;
    }
    public String getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;

	}

	@Override
	public String toString() {
		return "StartMDMRequestDTO [modifiedBy=" + modifiedBy + ", customerName=" + customerName + ", customerType="
				+ customerType + ", customerGroup=" + customerGroup + ", territory=" + territory + ", leadName="
				+ leadName + ", prospectName=" + prospectName + ", accountManager=" + accountManager
				+ ", defaultCurrency=" + defaultCurrency + ", defaultBankAccount=" + defaultBankAccount
				+ ", defaultPriceList=" + defaultPriceList + ", isInternalCustomer=" + isInternalCustomer
				+ ", marketSegment=" + marketSegment + ", industry=" + industry + ", website=" + website + ", language="
				+ language + ", customerDetails=" + customerDetails + ", primaryAddress=" + primaryAddress
				+ ", mobileNo=" + mobileNo + ", emailId=" + emailId + ", gstn=" + gstn + ", pan=" + pan
				+ ", disabled=" + disabled + ", risk=" + risk + ", status=" + status + ", createdBy=" + createdBy
				+ ", creationTime=" + creationTime + ", modificationTime=" + modificationTime + ", reviewStatus="
				+ reviewStatus + "]";
	}
   
}