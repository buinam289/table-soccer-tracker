package com.example.tablesoccer.controller;

import com.example.tablesoccer.model.Debt;
import com.example.tablesoccer.model.DebtResponse;
import com.example.tablesoccer.model.PaymentRequest;
import com.example.tablesoccer.service.DebtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/debts")
@Validated
public class DebtController {
    private final DebtService debtService;

    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Debt>> getAllDebts() {
        try {
            return ResponseEntity.ok(debtService.getAllDebts());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<DebtResponse> getPlayerDebts(@RequestParam String player) {
        try {
            return ResponseEntity.ok(debtService.getPlayerDebts(player));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/pay")
    public ResponseEntity<Void> recordPayment(@Valid @RequestBody PaymentRequest payment) {
        try {
            debtService.recordPayment(payment.getFrom(), payment.getTo(), payment.getAmount());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}