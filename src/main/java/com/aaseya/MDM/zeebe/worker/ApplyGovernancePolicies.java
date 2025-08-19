package com.aaseya.MDM.zeebe.worker;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import com.aaseya.MDM.dao.CustomerDAO;
import com.aaseya.MDM.model.Customer;
import com.aaseya.MDM.service.CustomerService;

@Component
public class ApplyGovernancePolicies {

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private CustomerService customerService;
	
	@PersistenceContext
    private EntityManager entityManager;

	@JobWorker(type = "ApplyGovernancePolicies")
	@Transactional
    public void handleJob(final JobClient client, final ActivatedJob job) {
        String businessKey = job.getVariablesAsMap().get("MDMBusinessKey").toString();
        String customerIdStr = businessKey.replace("MDM", "");
        Long customerId = Long.parseLong(customerIdStr);

        // Directly updating Customer table using CriteriaBuilder
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Customer> update = cb.createCriteriaUpdate(Customer.class);
        Root<Customer> root = update.from(Customer.class);

        update.set("status", "in_progress")  // Set updated status
        	  .set("reviewStatus", "approved") // Set reviewStatus to approved
              .where(cb.equal(root.get("customerId"), customerId));  // Find customer by ID

        int updatedCount = entityManager.createQuery(update).executeUpdate();
        entityManager.flush();  // Ensure the update is pushed to DB immediately
        System.out.println(updatedCount);
        if (updatedCount > 0) {
            // Ensure Camunda variable update happens before completion
            Map<String, Object> processVariables = new HashMap<>(job.getVariablesAsMap());
            processVariables.put("status", "in_progress"); // Updating process variable
            processVariables.put("reviewStatus", "approved");

            client.newCompleteCommand(job.getKey())
                  .variables(processVariables) // Explicitly pass all variables
                  .send()
                  .join();
        } else {
            client.newFailCommand(job.getKey()).retries(0).send().join();
        }
    }
	}
