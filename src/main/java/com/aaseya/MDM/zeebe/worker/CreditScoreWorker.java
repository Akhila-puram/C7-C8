package com.aaseya.MDM.zeebe.worker;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import com.aaseya.MDM.dao.CustomerDAO;
import com.aaseya.MDM.model.Customer;

@Component
public class CreditScoreWorker {

	@Autowired
	private CustomerDAO customerDAO; // Inject the CustomerDAO to interact with the database

	@JobWorker(type = "credit-score")
	public void assignCreditScore(final JobClient client, final ActivatedJob job) {
		try {
			System.out.println("Job Worker started: Processing credit-score job");

			// Step 1: Get process instance key (used as customerId)
			long processInstanceKey = job.getProcessInstanceKey();
			Long customerId = processInstanceKey;
			System.out.println("Process Instance Key / Customer ID: " + customerId);

			// Step 2: Get variables
			Map<String, Object> variables = job.getVariablesAsMap();
			System.out.println("Retrieved variables: " + variables);
			String pan = (String) variables.get("pan");
			System.out.println("PAN from variables: " + pan);

			// Step 3: Determine credit score
			int creditScore = determineCreditScore(pan);
			System.out.println("Calculated Credit Score: " + creditScore);

			// Step 4: Update Zeebe variables
			client.newCompleteCommand(job.getKey())
					.variables(Map.of("creditScore", creditScore))
					.send().join();
			System.out.println("Credit score updated in Camunda");

			// Step 5: Update customer in DB
			Customer customer = customerDAO.findCustomerById(customerId);
			if (customer != null) {
				System.out.println("Customer found: " + customer);
				customer.setCreditScore(String.valueOf(creditScore));
				customerDAO.saveCustomerCreditScore(customer);
				System.out.println("Credit score updated in DB");
			} else {
				System.out.println("No customer found for ID: " + customerId);
			}
		} catch (Exception e) {
			System.out.println("Exception in Job Worker: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private int determineCreditScore(String pan) {
		if (pan == null || pan.length() < 2) {
			System.out.println("Invalid PAN: " + pan);
			return 0;
		}
		String prefix = pan.substring(0, 2).toUpperCase();
		return switch (prefix) {
			case "BK" -> 80;
			case "DN" -> 50;
			case "RB" -> 20;
			case "DS" -> 10;
			default -> 0;
		};
	}
}
