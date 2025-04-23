package com.example.tablesoccer.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {
    @NotBlank(message = "From player is required")
    private String from;
    
    @NotBlank(message = "To player is required")
    private String to;
    
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be at least 1")
    private Integer amount;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}