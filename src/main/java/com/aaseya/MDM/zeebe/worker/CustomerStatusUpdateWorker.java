package com.aaseya.MDM.zeebe.worker;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import com.aaseya.MDM.dao.CustomerDAO;
import com.aaseya.MDM.model.Customer;
import com.aaseya.MDM.service.CustomerService;

@Component
public class CustomerStatusUpdateWorker {

    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private CustomerService customerService;

    @JobWorker(type = "updateCustomerStatus")
    public void handleJob(final JobClient client, final ActivatedJob job) {
        String businessKey = job.getVariablesAsMap().get("MDMBusinessKey").toString();
        String customerIdStr = businessKey.replace("MDM", "");
        Long customerId = Long.parseLong(customerIdStr);

        // Update both status and reviewStatus in DB inside a single transaction
        boolean statusUpdated = customerService.updateCustomerStatus(customerId, "pending");
        boolean reviewStatusUpdated = customerService.updateCustomerReviewStatus(customerId, "open");

        if (statusUpdated && reviewStatusUpdated) {
            // Ensure both values are set in process variables
            Map<String, Object> variables = new HashMap<>();
            variables.put("status", "pending");
            variables.put("reviewStatus", "open");

            client.newCompleteCommand(job.getKey())
                  .variables(variables)
                  .send()
                  .join();
        } else {
            client.newFailCommand(job.getKey()).retries(0).send().join();
        }
    }
}
