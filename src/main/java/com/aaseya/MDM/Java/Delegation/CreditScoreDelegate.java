package com.aaseya.MDM.Java.Delegation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.aaseya.MDM.dao.CustomerDAO;
import com.aaseya.MDM.model.Customer;

@Component
public class CreditScoreDelegate implements JavaDelegate {

    @Autowired
    private CustomerDAO customerDAO; // Inject the CustomerDAO to interact with the database

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        try {
            System.out.println("Java Delegate started: Processing credit-score task");

            // Step 1: Get process instance ID (used as customerId)
            String processInstanceId = execution.getProcessInstanceId();
            Long customerId = Long.valueOf(processInstanceId); // Assuming processInstanceId is the customerId
            System.out.println("Process Instance ID / Customer ID: " + customerId);

            // Step 2: Get variables
            String pan = (String) execution.getVariable("pan");
            System.out.println("PAN from variables: " + pan);

            // Step 3: Determine credit score
            int creditScore = determineCreditScore(pan);
            System.out.println("Calculated Credit Score: " + creditScore);

            // Step 4: Update process variables
            execution.setVariable("creditScore", creditScore);
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
            System.out.println("Exception in Java Delegate: " + e.getMessage());
            e.printStackTrace();
            throw e; // Rethrow the exception to signal failure
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
