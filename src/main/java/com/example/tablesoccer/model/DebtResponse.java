package com.example.tablesoccer.model;

import java.util.List;

public class DebtResponse {
    private List<Debt> owes;
    private List<Debt> owedBy;

    public DebtResponse(List<Debt> owes, List<Debt> owedBy) {
        this.owes = owes;
        this.owedBy = owedBy;
    }

    public List<Debt> getOwes() {
        return owes;
    }

    public List<Debt> getOwedBy() {
        return owedBy;
    }
}