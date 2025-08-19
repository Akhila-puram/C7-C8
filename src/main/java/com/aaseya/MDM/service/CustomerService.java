package com.aaseya.MDM.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aaseya.MDM.dao.CustomerDAO;
import com.aaseya.MDM.dto.StartMDMRequestDTO;
import com.aaseya.MDM.dto.CustomerDetailsDTO;
import com.aaseya.MDM.dto.CustomerResponseDTO;
import com.aaseya.MDM.dto.CustomerStatusCountDTO;
import com.aaseya.MDM.model.Customer;

@Service
public class CustomerService {

	@Autowired
	private CustomerDAO customerDAO;

	public boolean saveCustomer(final StartMDMRequestDTO startMDMRequestDTO, final String businessKey) {
		boolean result = true;
		try {
			Customer customer = new Customer();
			String customerId = businessKey.replace("MDM", "");
			System.out.println(Long.parseLong(customerId));
			startMDMRequestDTO.setLanguage("en");
			startMDMRequestDTO.setDisabled(0);
			startMDMRequestDTO.setStatus("in_progress");
			customer.setCustomerId(Long.parseLong(customerId));
			customer.setCustomer_name(startMDMRequestDTO.getCustomerName());
			customer.setCustomer_type(startMDMRequestDTO.getCustomerType());
			customer.setCustomer_group(startMDMRequestDTO.getCustomerGroup());
			customer.setPrimary_address(startMDMRequestDTO.getPrimaryAddress());
			customer.setMobile_no(startMDMRequestDTO.getMobileNo());
			customer.setRisk(startMDMRequestDTO.getRisk());
			customer.setStatus(startMDMRequestDTO.getStatus());
			customer.setCreated_by(startMDMRequestDTO.getCreatedBy());
			customer.setModified_by(startMDMRequestDTO.getModifiedBy());
			customer.setTerritory(startMDMRequestDTO.getTerritory());
			customer.setLead_name(startMDMRequestDTO.getLeadName());
			customer.setProspect_name(startMDMRequestDTO.getProspectName());
			customer.setAccount_manager(startMDMRequestDTO.getAccountManager());
			customer.setDefault_currency(startMDMRequestDTO.getDefaultCurrency());
			customer.setDefault_bank_account(startMDMRequestDTO.getDefaultBankAccount());
			customer.setDefault_price_list(startMDMRequestDTO.getDefaultPriceList());
			customer.setIs_internal_customer(startMDMRequestDTO.getIsInternalCustomer());
			customer.setMarket_segment(startMDMRequestDTO.getMarketSegment());
			customer.setIndustry(startMDMRequestDTO.getIndustry());
			customer.setWebsite(startMDMRequestDTO.getWebsite());
			customer.setLanguage(startMDMRequestDTO.getLanguage());
			customer.setCustomer_details(startMDMRequestDTO.getCustomerDetails());
			customer.setEmail_id(startMDMRequestDTO.getEmailId());
			customer.setGstn(startMDMRequestDTO.getGstn());
			customer.setPan(startMDMRequestDTO.getPan());
			customer.setDisabled(startMDMRequestDTO.getDisabled());
			customer.setReviewStatus(startMDMRequestDTO.getReviewStatus());

			// Formatting creation and modification timestamps
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

			customer.setCreationTime(
					LocalDateTime.parse(startMDMRequestDTO.getCreationTime().format(formatter), formatter));
			customer.setModificationTime(
					LocalDateTime.parse(startMDMRequestDTO.getModificationTime().format(formatter), formatter));

			customerDAO.saveCustomer(customer);
			System.out.println(customer);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

//////for Updating the Customer Status//////
	public boolean updateCustomerStatus(Long customerId, String newStatus) {
		boolean result = true;
		try {
			// Find the customer by ID
			Customer customer = customerDAO.findById(customerId);
			if (customer != null) {
				customer.setStatus(newStatus);
				customerDAO.saveCustomer(customer); // Save the updated customer
			} else {
				result = false; // Customer not found
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	////// for Updating the Customer Status//////

//////for Updating the Customer reviewStatus//////
	@Transactional // Ensures both updates are committed together
	public boolean updateCustomerReviewStatus(Long customerId, String newReviewStatus) {
		try {
			// Find the customer by ID
			Customer customer = customerDAO.findById(customerId);
			if (customer != null) {
				customer.setReviewStatus(newReviewStatus);
				customerDAO.saveCustomer(customer); // Save the updated customer
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false; // Customer not found or error occurred
	}
	////// for Updating the Customer reviewStatus//////

	///// getAllCustomers//////

	public Page<CustomerDetailsDTO> getAllCustomers(Pageable pageable) {
		return customerDAO.getAllCustomerDetails(pageable);
	}

	/// getcasesbystatus//
	@Transactional(readOnly = true)
	public CustomerStatusCountDTO getCustomerStatusCounts() {
		return customerDAO.getCustomerStatusCounts();
	}

	public Page<CustomerDetailsDTO> getCustomerForReview(Pageable pageable) {
		return customerDAO.getCustomerForReview(pageable);
	}

	// getCasesbyReviewstatus

	public Map<String, Long> getCasesCountByReviewStatus() {
		return customerDAO.getCasesCountByReviewStatus();
	}

	public CustomerResponseDTO getCustomerForReview(Long customerId) {
		Customer customer = customerDAO.findCustomerById(customerId);

		CustomerResponseDTO response = new CustomerResponseDTO();

		// General Details
		CustomerResponseDTO.GeneralDetails general = new CustomerResponseDTO.GeneralDetails();
		general.setCustomerId(customer.getCustomerId());
		general.setCustomerName(customer.getCustomer_name());
		general.setCustomerType(customer.getCustomer_type());
		general.setCustomerGroup(customer.getCustomer_group());
		general.setTerritory(customer.getTerritory());
		general.setMarketSegment(customer.getMarket_segment());
		general.setIndustry(customer.getIndustry());
		general.setWebsite(customer.getWebsite());
		general.setCustomerAddress(customer.getPrimary_address());
		response.setGeneralDetails(general);

		// Financial Details
		CustomerResponseDTO.FinancialDetails financial = new CustomerResponseDTO.FinancialDetails();
		financial.setDefaultCurrency(customer.getDefault_currency());
		financial.setDefaultPriceList(customer.getDefault_price_list());
		financial.setGstn(customer.getGstn());
		financial.setPanNumber(customer.getPan());
		financial.setCreditScore(customer.getCreditScore());
		response.setFinancialDetails(financial);

		// Contact Details
		CustomerResponseDTO.ContactDetails contact = new CustomerResponseDTO.ContactDetails();
		contact.setPhoneNumber(String.valueOf(customer.getMobile_no()));
		contact.setEmailId(customer.getEmail_id());
		contact.setAccountManager(customer.getAccount_manager());
		contact.setPrimaryAddress(customer.getPrimary_address());
		response.setContactDetails(contact);

		// Additional Details
		CustomerResponseDTO.AdditionalDetails additional = new CustomerResponseDTO.AdditionalDetails();
		additional.setIsInternalCustomer(customer.getIs_internal_customer() == 1 ? "Yes" : "No");
		additional.setModifiedBy(customer.getModified_by());
		response.setAdditionalDetails(additional);

		return response;
	}
	
	@Transactional
	public boolean updateCustomerRisk(Long customerId, String newRisk) {
	    try {
	        Customer customer = customerDAO.findById(customerId);
	        if (customer != null) {
	            customer.setRisk(newRisk);
	            customerDAO.saveCustomer(customer);
	            return true;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}

}
