package com.paymentic.wallet.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class PayoutRequest {
    
    @JsonProperty("note")
    private String note;
    
    @NotNull
    @Valid
    @JsonProperty("amount")
    private AmountDto amount;
    
    @NotNull
    @Valid
    @JsonProperty("seller")
    private SellerDto seller;

    public PayoutRequest() {}

    public PayoutRequest(String note, AmountDto amount, SellerDto seller) {
        this.note = note;
        this.amount = amount;
        this.seller = seller;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public AmountDto getAmount() {
        return amount;
    }

    public void setAmount(AmountDto amount) {
        this.amount = amount;
    }

    public SellerDto getSeller() {
        return seller;
    }

    public void setSeller(SellerDto seller) {
        this.seller = seller;
    }

    public static class AmountDto {
        @NotNull
        @JsonProperty("currency")
        private String currency;
        
        @NotNull
        @JsonProperty("value")
        private String value;

        public AmountDto() {}

        public AmountDto(String currency, String value) {
            this.currency = currency;
            this.value = value;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class SellerDto {
        @NotNull
        @JsonProperty("account")
        private String account;

        public SellerDto() {}

        public SellerDto(String account) {
            this.account = account;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }
    }
}