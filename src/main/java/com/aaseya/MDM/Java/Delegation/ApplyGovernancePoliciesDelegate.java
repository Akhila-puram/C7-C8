package com.aaseya.MDM.Java.Delegation;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aaseya.MDM.dao.CustomerDAO;
import com.aaseya.MDM.model.Customer;
import com.aaseya.MDM.service.CustomerService;

@Component("applyGovernancePoliciesDelegate")
public class ApplyGovernancePoliciesDelegate implements JavaDelegate {

    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private CustomerService customerService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void execute(DelegateExecution execution) throws Exception {
        // Example: Access model if needed
        BpmnModelInstance modelInstance = execution.getBpmnModelInstance();
        ServiceTask serviceTask = (ServiceTask) execution.getBpmnModelElementInstance();

        // Extract business key and customer ID
        String businessKey = (String) execution.getVariable("MDMBusinessKey");
        String customerIdStr = businessKey.replace("MDM", "");
        Long customerId = Long.parseLong(customerIdStr);

        // Build and execute update query
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Customer> update = cb.createCriteriaUpdate(Customer.class);
        Root<Customer> root = update.from(Customer.class);

        update.set("status", "in_progress")
              .set("reviewStatus", "approved")
              .where(cb.equal(root.get("customerId"), customerId));

        int updatedCount = entityManager.createQuery(update).executeUpdate();
        entityManager.flush();

        System.out.println("Records updated: " + updatedCount);

        // Set process variables if update succeeded
        if (updatedCount > 0) {
            execution.setVariable("status", "in_progress");
            execution.setVariable("reviewStatus", "approved");
        } else {
            throw new RuntimeException("Customer not found or update failed.");
        }
    }
}
