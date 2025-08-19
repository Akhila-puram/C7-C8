package com.aaseya.MDM.zeebe.worker;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

@Service
public class PotentialMatchZeebeWorker {

    private static final Logger logger = LoggerFactory.getLogger(PotentialMatchZeebeWorker.class);
    private final ZeebeClient zeebeClient;

    public PotentialMatchZeebeWorker(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @JobWorker(type = "check-potential-match", autoComplete = false)
    public void checkPotentialMatch(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        logger.info("Received Job Variables: {}", variables);

        String newCustomerDetails = (String) variables.get("customer_details"); 
        String customerId = (String) variables.get("customerId"); 

        if (newCustomerDetails == null || newCustomerDetails.trim().isEmpty()) {
            logger.warn("No customer_details provided. Approving by default.");
            updateAndComplete(client, job, variables, "approved", false);
            return;
        }

        Map<?, ?> customerDataMap = (variables.get("customerData") instanceof Map<?, ?>) ? (Map<?, ?>) variables.get("customerData") : null;
        if (customerDataMap == null) {
            logger.warn("Invalid or missing customerData. Approving.");
            updateAndComplete(client, job, variables, "approved", false);
            return;
        }

        Map<?, ?> responseBody = (customerDataMap.get("body") instanceof Map<?, ?>) ? (Map<?, ?>) customerDataMap.get("body") : null;
        if (responseBody == null) {
            logger.warn("Invalid customerData body. Approving.");
            updateAndComplete(client, job, variables, "approved", false);
            return;
        }

        List<?> existingCustomers = (responseBody.get("data") instanceof List<?>) ? (List<?>) responseBody.get("data") : null;
        if (existingCustomers == null) {
            logger.warn("Invalid customerData list. Approving.");
            updateAndComplete(client, job, variables, "approved", false);
            return;
        }

        boolean matchFound = existingCustomers.stream()
                .peek(existingCustomer -> logger.info("Checking customer: {}", existingCustomer))
                .filter(existingCustomer -> existingCustomer instanceof Map<?, ?>)
                .map(existingCustomer -> (Map<?, ?>) existingCustomer)
                .map(customerMap -> (String) customerMap.get("customer_details"))
                .peek(existingCustomerDetails -> logger.info("Comparing details: {}", existingCustomerDetails))
                .anyMatch(existingCustomerDetails -> existingCustomerDetails != null && existingCustomerDetails.equalsIgnoreCase(newCustomerDetails));

        variables.put("matchFound", matchFound);

        if (matchFound) {
            logger.info("Duplicate found! Marking as rejected.");
            variables.put("status", "rejected");

            if (customerId == null || customerId.trim().isEmpty()) {
                logger.error("Cannot send message: customerId is missing.");
            } else {
                sendMatchFoundMessage(customerId);
            }
        } else {
            logger.info("No duplicate found. Approving the request.");
            variables.put("status", "approved");
        }

        updateAndComplete(client, job, variables, variables.get("status").toString(), matchFound);
    }

    private void updateAndComplete(JobClient client, ActivatedJob job, Map<String, Object> variables, String status, boolean matchFound) {
        variables.put("status", status);
        variables.put("matchFound", matchFound);
        client.newCompleteCommand(job.getKey())
              .variables(variables)
              .send()
              .join();
        logger.info("Job completed with status: {} and matchFound: {}", status, matchFound);
    }

    private void sendMatchFoundMessage(String customerId) {
        zeebeClient.newPublishMessageCommand()
            .messageName("MatchFoundMessage")  
            .correlationKey(customerId)  
            .send()
            .join();

        logger.info("Message 'MatchFoundMessage' sent with correlationKey: {}", customerId);
    }
}
