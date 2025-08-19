package com.aaseya.MDM.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaseya.MDM.dto.StartMDMRequestDTO;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

@Service
public class MDMService {

    @Autowired
    private ZeebeClient zeebeClient;

    public String startMDMProcess(StartMDMRequestDTO startMDMRequestDTO) {
        String businessKey = "";
        Map<String, Object> variables = new HashMap<>();
        startMDMRequestDTO.setLanguage("en");
        startMDMRequestDTO.setDisabled(0);
        startMDMRequestDTO.setStatus("in_progress");
        variables.put("customerName", startMDMRequestDTO.getCustomerName());
        variables.put("customerType", startMDMRequestDTO.getCustomerType());
        variables.put("customerGroup", startMDMRequestDTO.getCustomerGroup());
        variables.put("primaryAddress", startMDMRequestDTO.getPrimaryAddress());
        variables.put("mobileNo", startMDMRequestDTO.getMobileNo());
        variables.put("risk", startMDMRequestDTO.getRisk());
        variables.put("status", startMDMRequestDTO.getStatus());
        variables.put("createdBy", startMDMRequestDTO.getCreatedBy());
        variables.put("creationTime", startMDMRequestDTO.getCreationTime());
        variables.put("modifiedBy", startMDMRequestDTO.getModifiedBy());
        variables.put("modifiedTime", startMDMRequestDTO.getModificationTime());
        variables.put("territory", startMDMRequestDTO.getTerritory());
        variables.put("leadName", startMDMRequestDTO.getLeadName());
        variables.put("prospectName", startMDMRequestDTO.getProspectName());
        variables.put("defaultCurrency", startMDMRequestDTO.getDefaultCurrency());
        variables.put("defaultBankAccount", startMDMRequestDTO.getDefaultBankAccount());
        variables.put("defaultPriceList", startMDMRequestDTO.getDefaultPriceList());
        variables.put("isInternalCustomer", startMDMRequestDTO.getIsInternalCustomer());
        variables.put("marketSegment", startMDMRequestDTO.getMarketSegment());
        variables.put("industry", startMDMRequestDTO.getIndustry());
        variables.put("website", startMDMRequestDTO.getWebsite());
        variables.put("language", startMDMRequestDTO.getLanguage());
        variables.put("accountManager", startMDMRequestDTO.getAccountManager());
        variables.put("customerDetails", startMDMRequestDTO.getCustomerDetails());
        variables.put("emailId", startMDMRequestDTO.getEmailId());
        variables.put("gstn", startMDMRequestDTO.getGstn());
        variables.put("pan", startMDMRequestDTO.getPan());
        variables.put("disabled", startMDMRequestDTO.getDisabled());
        variables.put("reviewStatus", startMDMRequestDTO.getReviewStatus());
   
        ProcessInstanceEvent processInstanceEvent = zeebeClient
                .newCreateInstanceCommand()
                .bpmnProcessId("customerDataManagement")
                .latestVersion()
                .variables(variables)
                .send()
                .join();
        System.out.println(processInstanceEvent.getProcessInstanceKey());
        businessKey = "MDM" + processInstanceEvent.getProcessInstanceKey();
        variables.put("MDMBusinessKey", businessKey);
        zeebeClient.newSetVariablesCommand(processInstanceEvent.getProcessInstanceKey()).variables(variables).send().join();
        
        return businessKey;
    }
}
