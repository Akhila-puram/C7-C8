package com.aaseya.MDM.dto;

public class MDMResponseDTO {
    private String status;
    private String businessKey;
    private String message;
    

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

 
    @Override
    public String toString() {
        return "MDMResponseDTO [status=" + status + ", businessKey=" + businessKey + ", message=" + message
                + "]";
    }
}
