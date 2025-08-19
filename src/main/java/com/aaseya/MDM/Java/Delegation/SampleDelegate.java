package com.aaseya.MDM.Java.Delegation;



import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class SampleDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        // Get process variables
        String customerName = (String) execution.getVariable("customerName");
        Integer orderAmount = (Integer) execution.getVariable("orderAmount");

        // Business logic (simple example)
        String status = orderAmount > 1000 ? "HIGH_VALUE" : "NORMAL";

        // Set process variable
        execution.setVariable("orderStatus", status);

        // Log to console (optional)
        System.out.println("Customer: " + customerName);
        System.out.println("Order Amount: " + orderAmount);
        System.out.println("Order Status set to: " + status);
    }
}
