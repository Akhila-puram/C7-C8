 package com.aaseya.MDM.controller;

import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import org.springframework.http.HttpStatus;

import com.aaseya.MDM.dto.CustomerDetailsDTO;
import com.aaseya.MDM.dto.CustomerResponseDTO;
import com.aaseya.MDM.dto.CustomerStatusCountDTO;
import com.aaseya.MDM.dto.MDMResponseDTO;
import com.aaseya.MDM.dto.StartMDMRequestDTO;
import com.aaseya.MDM.service.CustomerService;
import com.aaseya.MDM.service.MDMService;
import com.aaseya.MDM.service.TaskListService;

@CrossOrigin("*")
@RestController
public class MDMController {
	
	 @Autowired
	    private MDMService mdmService;

	    @Autowired
	    private CustomerService customerService;
	    
	    @Autowired
	    private TaskListService taskListService;
	    
	    @PostMapping("/startMDMProcess")
	    public MDMResponseDTO startMDMProcess(@RequestBody StartMDMRequestDTO startMDMRequest) {
	        MDMResponseDTO responseDTO = new MDMResponseDTO();
	        try {
	            String businessKey = mdmService.startMDMProcess(startMDMRequest);
	            responseDTO.setStatus("Success");
	            responseDTO.setMessage("MDM process started successfully.");
	            responseDTO.setBusinessKey(businessKey);
	            // Store customer details in the database
	            boolean result = customerService.saveCustomer(startMDMRequest, businessKey);
	            if (!result) {
	                responseDTO.setStatus("Failure");
	                responseDTO.setMessage("Error while saving customer details.");
	            }

	            System.out.println(responseDTO);
	        } catch (Exception e) {
	            e.printStackTrace();
	            responseDTO.setStatus("Failure");
	            responseDTO.setMessage("MDM process failed.");
	        }
	        return responseDTO;
	    }
	    @GetMapping("/getAllCustomers")
	    public Page<CustomerDetailsDTO> getAllCustomerDetails(
	            @RequestParam(defaultValue = "0") int page,
	            @RequestParam(defaultValue = "10") int size) {

	        Pageable pageable = PageRequest.of(page, size);
	        return customerService.getAllCustomers(pageable);
	    }
	    

	    @GetMapping("/getcasesbystatus")
	    public CustomerStatusCountDTO getCustomerStatusCounts() {
	        return customerService.getCustomerStatusCounts();
	    }
	    

	    @GetMapping("/getCustomerForReview")
	    public Page<CustomerDetailsDTO> getCustomerForReview(
	            @RequestParam(defaultValue = "0") int page,
	            @RequestParam(defaultValue = "10") int size) {

	        Pageable pageable = PageRequest.of(page, size);
	        return customerService.getCustomerForReview(pageable);
	    }
  
	  //getCasesbyReviewstatus
	    @GetMapping("/getCasesbyReviewstatus")
	    public Map<String, Long> getCasesCountByReviewStatus() {
	        return customerService.getCasesCountByReviewStatus();
	    }
	    
 ///Get the customer details by customerId for review
	    
	    @GetMapping("/getCustomerById/{customerId}")
	    public ResponseEntity<CustomerResponseDTO> getCustomerForReview(@PathVariable Long customerId) {
	        CustomerResponseDTO response = customerService.getCustomerForReview(customerId);
	        return ResponseEntity.ok(response);
	    }

	    
	    
	    @PostMapping("/submitReview")
	    public ResponseEntity<Map<String, Object>> approveRejectCustomer(
	            @RequestParam("customerId") String customerId,
	            @RequestParam("action") String action) {
 
	        Map<String, Object> response = new HashMap<>();
 
	        if (!action.equalsIgnoreCase("approve") && !action.equalsIgnoreCase("reject")) {
	            response.put("status", "error");
	            response.put("message", "Invalid action. Use 'approve' or 'reject'");
	            return ResponseEntity.badRequest().body(response);
	        }
 
	        Map<String, Object> variables = new HashMap<>();
	        if (action.equalsIgnoreCase("approve")) {
	            variables.put("approved", true);
	        } else if (action.equalsIgnoreCase("reject")) {
	            variables.put("approved", false);
	        }
 
	        try {
	            String taskId = taskListService.getActiveTaskID(customerId);
 
	            if (taskId == null || taskId.isEmpty()) {
	                response.put("status", "error");
	                response.put("message", "No active task found for customer ID: " + customerId);
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	            }
 
	            String result = taskListService.CompleteTaskByIDReview(taskId, variables);
 
	            if (result.contains("Error")) {
	                response.put("status", "error");
	                response.put("message", result);
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	            }
 
	            response.put("status", "success");
	            response.put("message", "Task completed successfully. Action: " + action + ", Customer ID: " + customerId);
	            return ResponseEntity.ok(response);
 
	        } catch (Exception e) {
	            response.put("status", "error");
	            response.put("message", "Error processing request: " + e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	        }
	    }
	}
