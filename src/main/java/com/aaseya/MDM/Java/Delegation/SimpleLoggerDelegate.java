package com.aaseya.MDM.Java.Delegation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class SimpleLoggerDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        
        
        System.out.println("Executing Service Task...");
        

        // Set a process variable
        execution.setVariable("status", "Processed");
    }
}