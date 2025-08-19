package com.aaseya.MDM.zeebe.worker;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aaseya.MDM.dao.CustomerDAO;
import com.aaseya.MDM.model.Customer;
import com.aaseya.MDM.service.CustomerService;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Component
public class RejectionListener {

    @Autowired
    private CustomerService customerService;

    @PersistenceContext
    private EntityManager entityManager;

    @JobWorker(type = "CustomerRejectionStatus")
    @Transactional
    public void handleJob(final JobClient client, final ActivatedJob job) {
        String businessKey = job.getVariablesAsMap().get("MDMBusinessKey").toString();
        String customerIdStr = businessKey.replace("MDM", "");
        Long customerId = Long.parseLong(customerIdStr);

        Boolean approved = (Boolean) job.getVariablesAsMap().get("approved");  // Read approved flag

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Customer> update = cb.createCriteriaUpdate(Customer.class);
        Root<Customer> root = update.from(Customer.class);

        if (Boolean.FALSE.equals(approved)) {  // If approved is false
            update.set("status", "rejected")
                  .set("reviewStatus", "rejected")
                  .where(cb.equal(root.get("customerId"), customerId));
        } else { // If approved is not false
            update.set("status", "rejected")
                  .where(cb.equal(root.get("customerId"), customerId));
        }

        int updatedCount = entityManager.createQuery(update).executeUpdate();
        entityManager.flush();

        if (updatedCount > 0) {
            Map<String, Object> processVariables = new HashMap<>(job.getVariablesAsMap());
            processVariables.put("status", "rejected");

            if (Boolean.FALSE.equals(approved)) {
                processVariables.put("reviewStatus", "rejected");
            }

            client.newCompleteCommand(job.getKey())
                  .variables(processVariables)
                  .send()
                  .join();
        } else {
            client.newFailCommand(job.getKey()).retries(0).send().join();
        }
    }
}
