package com.aaseya.MDM.zeebe.worker;

import java.util.Map;
import java.util.HashMap;

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

import com.aaseya.MDM.model.Customer;
import com.aaseya.MDM.dao.CustomerDAO;

@Component
public class UpdateRiskScore {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CustomerDAO customerDAO;

    @JobWorker(type = "UpdateRiskScore")
    @Transactional
    public void handleJob(final JobClient client, final ActivatedJob job) {
        try {
            // Get all process variables
            Map<String, Object> variables = job.getVariablesAsMap();

            // Extract processInstanceKey (used as customerId)
            long processInstanceKey = job.getProcessInstanceKey();
            Long customerId = processInstanceKey;  // assuming direct mapping

            // Extract risk score from variables
            Object riskValue = variables.get("risk");
            if (riskValue == null) {
                throw new IllegalArgumentException("Missing 'risk' variable in process.");
            }

            String risk = riskValue.toString();

            // JPA update using CriteriaBuilder
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaUpdate<Customer> update = cb.createCriteriaUpdate(Customer.class);
            Root<Customer> root = update.from(Customer.class);

            update.set("risk", risk)
                  .where(cb.equal(root.get("customerId"), customerId));

            int updatedCount = entityManager.createQuery(update).executeUpdate();
            entityManager.flush();

            if (updatedCount > 0) {
                // Update process variable (optional)
                Map<String, Object> updatedVars = new HashMap<>(variables);
                updatedVars.put("riskUpdated", true);

                client.newCompleteCommand(job.getKey())
                      .variables(updatedVars)
                      .send()
                      .join();
            } else {
                // Customer not found, fail the job
                client.newFailCommand(job.getKey())
                      .retries(0)
                      .errorMessage("Customer not found for ID: " + customerId)
                      .send()
                      .join();
            }

        } catch (Exception e) {
            // Fail job on any unexpected error
            client.newFailCommand(job.getKey())
                  .retries(0)
                  .errorMessage("Exception occurred: " + e.getMessage())
                  .send()
                  .join();
        }
    }
}
