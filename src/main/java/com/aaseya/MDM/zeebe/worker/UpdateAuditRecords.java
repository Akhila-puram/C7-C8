package com.aaseya.MDM.zeebe.worker;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import jakarta.persistence.NoResultException;

import com.aaseya.MDM.dao.CustomerDAO;
import com.aaseya.MDM.model.Customer;
import com.aaseya.MDM.service.CustomerService;

@Component
public class UpdateAuditRecords {

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private CustomerService customerService;

	@JobWorker(type = "UpdateAuditRecords")
	public void handleJob(final JobClient client, final ActivatedJob job) {
	    // Get process instance key and treat it as customerId
	    long processInstanceKey = job.getProcessInstanceKey();
	    Long customerId = processInstanceKey;

	    // Extract variables
	    Map<String, Object> variables = job.getVariablesAsMap();
	    String modifiedByFromProcess = (String) variables.get("modified_by");

	    System.out.println("Process Instance Key (Customer ID): " + customerId);
	    System.out.println("Modified By (from process): " + modifiedByFromProcess);

	    // Fetch customer by ID
	    Customer customer = null;
	    try {
	        customer = customerDAO.findById(customerId);
	    } catch (NoResultException e) {
	        System.err.println("Customer not found with ID: " + customerId);
	    }

	    if (customer != null) {
	        // Fallback: use existing DB value if modified_by not provided
	        String finalModifiedBy = (modifiedByFromProcess != null && !modifiedByFromProcess.trim().isEmpty())
	                                ? modifiedByFromProcess
	                                : customer.getModified_by();

	        LocalDateTime currentTime = LocalDateTime.now();
	        customer.setModificationTime(currentTime);
	        customer.setModified_by(finalModifiedBy);

	        customerDAO.saveCustomer(customer);

	        // Prepare updated process variables
	        Map<String, Object> updatedVariables = new HashMap<>();
	        updatedVariables.put("modifiedTime", currentTime.toString());  // For Camunda variable
	        updatedVariables.put("modified_by", finalModifiedBy);          // Keep it consistent

	        client.newCompleteCommand(job.getKey())
	              .variables(updatedVariables)
	              .send()
	              .join();
	    } else {
	        // Fail the job if customer not found
	        client.newFailCommand(job.getKey())
	              .retries(0)
	              .errorMessage("Customer not found with ID (processInstanceKey): " + customerId)
	              .send()
	              .join();
	    }
	}


}
