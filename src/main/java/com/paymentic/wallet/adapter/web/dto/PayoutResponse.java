package com.paymentic.wallet.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PayoutResponse {
    
    @JsonProperty("transactionId")
    private String transactionId;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("note")
    private String note;

    public PayoutResponse() {}

    public PayoutResponse(String transactionId, String status, String note) {
        this.transactionId = transactionId;
        this.status = status;
        this.note = note;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}