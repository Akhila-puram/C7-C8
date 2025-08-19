package com.aaseya.MDM.Java.Delegation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.aaseya.MDM.dao.CustomerDAO;
import com.aaseya.MDM.service.CustomerService;

@Component
public class CustomerStatusUpdateDelegate implements JavaDelegate {

    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private CustomerService customerService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        try {
            // Step 1: Retrieve the business key from process variables
            String businessKey = (String) execution.getVariable("MDMBusinessKey");
            String customerIdStr = businessKey.replace("MDM", "");
            Long customerId = Long.parseLong(customerIdStr);

            // Step 2: Update both status and reviewStatus in DB inside a single transaction
            boolean statusUpdated = customerService.updateCustomerStatus(customerId, "pending");
            boolean reviewStatusUpdated = customerService.updateCustomerReviewStatus(customerId, "open");

            if (statusUpdated && reviewStatusUpdated) {
                // Step 3: Set updated values in process variables
                execution.setVariable("status", "pending");
                execution.setVariable("reviewStatus", "open");
                System.out.println("Customer status and review status updated successfully.");
            } else {
                throw new Exception("Failed to update customer status or review status.");
            }
        } catch (Exception e) {
            System.out.println("Exception in CustomerStatusUpdateDelegate: " + e.getMessage());
            e.printStackTrace();
            throw e; // Rethrow the exception to signal failure
        }
    }
}
