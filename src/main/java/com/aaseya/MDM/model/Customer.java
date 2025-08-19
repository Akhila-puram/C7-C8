package com.aaseya.MDM.model;

import java.time.LocalDateTime;
import java.util.Arrays;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "Customer")
public class Customer {

	@Id
	@Column(name = "customerId")
	private Long customerId;

	@Column(name = "modified_by")
	private String modified_by;

	@Column(name = "naming_series")
	private String naming_series;

	@Column(name = "customer_name")
	private String customer_name;

	@Column(name = "customer_type")
	private String customer_type;

	@Column(name = "customer_group")
	private String customer_group;

	@Column(name = "territory")
	private String territory;

	@Column(name = "lead_name")
	private String lead_name;

	@Column(name = "prospect_name")
	private String prospect_name;

	@Column(name = "account_manager")
	private String account_manager;

	@Column(name = "default_currency")
	private String default_currency;

	@Column(name = "default_bank_account")
	private String default_bank_account;

	@Column(name = "default_price_list")
	private String default_price_list;

	@Column(name = "is_internal_customer")
	private int is_internal_customer;

	@Column(name = "market_segment")
	private String market_segment;

	@Column(name = "industry")
	private String industry;

	@Column(name = "website")
	private String website;

	@Column(name = "language")
	private String language;

	@Column(name = "customer_details")
	private String customer_details;

	@Column(name = "primary_address")
	private String primary_address;

	@Column(name = "mobile_no")
	private long mobile_no;

	@Column(name = "email_id")
	private String email_id;

	@Column(name = "gstn")
	private String gstn;

	@Column(name = "pan")
	private String pan;

	@Column(name = "disabled")
	private int disabled;

	@Column(name = "image")
	private byte[] image;

	@Column(name = "risk")
	private String risk;

	@Column(name = "status")
	private String status;

	@Column(name = "created_by")
	private String created_by;

	@Column(name = "creationTime")
	private LocalDateTime creationTime;

	@Column(name = "modificationTime")
	private LocalDateTime modificationTime;
	
	@Column(name = "review_status")
	private String reviewStatus;
	
	@Column(name = "creditScore")
	private String creditScore;

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getCustomer_group() {
		return customer_group;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public void setCustomer_group(String customer_group) {
		this.customer_group = customer_group;
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

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public String getModified_by() {
		return modified_by;
	}

	public void setModified_by(String modified_by) {
		this.modified_by = modified_by;
	}

	public String getNaming_series() {
		return naming_series;
	}

	public void setNaming_series(String naming_series) {
		this.naming_series = naming_series;
	}

	public String getCustomer_type() {
		return customer_type;
	}

	public void setCustomer_type(String customer_type) {
		this.customer_type = customer_type;
	}

	public String getTerritory() {
		return territory;
	}

	public void setTerritory(String territory) {
		this.territory = territory;
	}

	public String getLead_name() {
		return lead_name;
	}

	public void setLead_name(String lead_name) {
		this.lead_name = lead_name;
	}

	public String getProspect_name() {
		return prospect_name;
	}

	public void setProspect_name(String prospect_name) {
		this.prospect_name = prospect_name;
	}

	public String getAccount_manager() {
		return account_manager;
	}

	public void setAccount_manager(String account_manager) {
		this.account_manager = account_manager;
	}

	public String getDefault_currency() {
		return default_currency;
	}

	public void setDefault_currency(String default_currency) {
		this.default_currency = default_currency;
	}

	public String getDefault_bank_account() {
		return default_bank_account;
	}

	public void setDefault_bank_account(String default_bank_account) {
		this.default_bank_account = default_bank_account;
	}

	public String getDefault_price_list() {
		return default_price_list;
	}

	public void setDefault_price_list(String default_price_list) {
		this.default_price_list = default_price_list;
	}

	public int getIs_internal_customer() {
		return is_internal_customer;
	}

	public void setIs_internal_customer(int is_internal_customer) {
		this.is_internal_customer = is_internal_customer;
	}

	public String getMarket_segment() {
		return market_segment;
	}

	public void setMarket_segment(String market_segment) {
		this.market_segment = market_segment;
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

	public String getCustomer_details() {
		return customer_details;
	}

	public void setCustomer_details(String customer_details) {
		this.customer_details = customer_details;
	}

	public String getPrimary_address() {
		return primary_address;
	}

	public void setPrimary_address(String primary_address) {
		this.primary_address = primary_address;
	}

	public long getMobile_no() {
		return mobile_no;
	}

	public void setMobile_no(long mobile_no) {
		this.mobile_no = mobile_no;
	}

	public String getEmail_id() {
		return email_id;
	}

	public void setEmail_id(String email_id) {
		this.email_id = email_id;
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

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
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
	
	

	public String getCreditScore() {
		return creditScore;
	}

	public void setCreditScore(String creditScore) {
		this.creditScore = creditScore;
	}

	@PrePersist
	public void prePersist() {
		System.out.println("called prepersist " + customerId);
		if (customerId != null && customerId > 0) {

		} else {
			System.out.println("setting customer id to null");
			customerId = generateLongFromUUID();
		}
	}

	private Long generateLongFromUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.getMostSignificantBits() & Long.MAX_VALUE; // Convert UUID to positive long
	}

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", modified_by=" + modified_by + ", naming_series="
				+ naming_series + ", customer_name=" + customer_name + ", customer_type=" + customer_type
				+ ", customer_group=" + customer_group + ", territory=" + territory + ", lead_name=" + lead_name
				+ ", prospect_name=" + prospect_name + ", account_manager=" + account_manager + ", default_currency="
				+ default_currency + ", default_bank_account=" + default_bank_account + ", default_price_list="
				+ default_price_list + ", is_internal_customer=" + is_internal_customer + ", market_segment="
				+ market_segment + ", industry=" + industry + ", website=" + website + ", language=" + language
				+ ", customer_details=" + customer_details + ", primary_address=" + primary_address + ", mobile_no="
				+ mobile_no + ", email_id=" + email_id + ", gstn=" + gstn + ", pan=" + pan + ", disabled=" + disabled
				+ ", image=" + Arrays.toString(image) + ", risk=" + risk + ", status=" + status + ", created_by="
				+ created_by + ", creationTime=" + creationTime + ", modificationTime=" + modificationTime
				+ ", reviewStatus=" + reviewStatus + ", creditScore=" + creditScore + "]";
	}
}
	  