package com.aaseya.MDM.Java.Delegation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleLoggingDelegate implements JavaDelegate {

	
	    @Override
	    public void execute(DelegateExecution execution) {
	        System.out.println("MySimpleDelegate executed.");
	    }
	}


