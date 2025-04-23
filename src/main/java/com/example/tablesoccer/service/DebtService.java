package com.example.tablesoccer.service;

import com.example.tablesoccer.model.Debt;
import com.example.tablesoccer.model.DebtResponse;
import com.example.tablesoccer.repository.DebtLedger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DebtService {
    private final DebtLedger debtLedger;

    public DebtService(DebtLedger debtLedger) {
        this.debtLedger = debtLedger;
    }

    public List<Debt> getAllDebts() throws IOException {
        return debtLedger.getAllDebts();
    }

    public DebtResponse getPlayerDebts(String player) throws IOException {
        List<Debt> allDebts = debtLedger.getAllDebts();
        List<Debt> owes = allDebts.stream()
            .filter(debt -> debt.getFrom().equals(player))
            .collect(Collectors.toList());
        List<Debt> owedBy = allDebts.stream()
            .filter(debt -> debt.getTo().equals(player))
            .collect(Collectors.toList());
        
        return new DebtResponse(owes, owedBy);
    }

    public void recordPayment(String from, String to, int amount) throws IOException {
        List<Debt> debts = new ArrayList<>(debtLedger.getAllDebts());
        
        // Create a new list to avoid concurrent modification
        List<Debt> updatedDebts = new ArrayList<>();
        
        boolean paymentProcessed = false;
        for (Debt debt : debts) {
            if (debt.getFrom().equals(from) && debt.getTo().equals(to)) {
                int newAmount = debt.getAmount() - amount;
                if (newAmount > 0) {
                    updatedDebts.add(new Debt(from, to, newAmount));
                }
                paymentProcessed = true;
            } else {
                updatedDebts.add(debt);
            }
        }

        debtLedger.saveDebts(updatedDebts);
    }
}