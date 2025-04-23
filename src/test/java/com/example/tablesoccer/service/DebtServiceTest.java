package com.example.tablesoccer.service;

import com.example.tablesoccer.model.Debt;
import com.example.tablesoccer.model.DebtResponse;
import com.example.tablesoccer.repository.DebtLedger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DebtServiceTest {
    @Mock
    private DebtLedger debtLedger;

    private DebtService debtService;

    @BeforeEach
    void setUp() {
        debtService = new DebtService(debtLedger);
    }

    @Test
    void testGetPlayerDebts() throws IOException {
        List<Debt> allDebts = Arrays.asList(
            new Debt("A", "B", 2),
            new Debt("C", "A", 1),
            new Debt("A", "D", 3)
        );
        
        when(debtLedger.getAllDebts()).thenReturn(allDebts);
        
        DebtResponse response = debtService.getPlayerDebts("A");
        
        assertEquals(2, response.getOwes().size());
        assertEquals(1, response.getOwedBy().size());
        
        assertTrue(response.getOwes().stream()
            .anyMatch(d -> d.getTo().equals("B") && d.getAmount() == 2));
        assertTrue(response.getOwes().stream()
            .anyMatch(d -> d.getTo().equals("D") && d.getAmount() == 3));
        assertTrue(response.getOwedBy().stream()
            .anyMatch(d -> d.getFrom().equals("C") && d.getAmount() == 1));
    }

    @Test
    void testRecordPayment() throws IOException {
        List<Debt> existingDebts = Arrays.asList(
            new Debt("A", "B", 3)
        );
        
        when(debtLedger.getAllDebts()).thenReturn(existingDebts);
        
        debtService.recordPayment("A", "B", 2);
        
        verify(debtLedger).saveDebts(argThat(debts -> 
            debts.size() == 1 &&
            debts.get(0).getFrom().equals("A") &&
            debts.get(0).getTo().equals("B") &&
            debts.get(0).getAmount() == 1
        ));
    }

    @Test
    void testRecordPaymentFullAmount() throws IOException {
        List<Debt> existingDebts = Arrays.asList(
            new Debt("A", "B", 2)
        );
        
        when(debtLedger.getAllDebts()).thenReturn(existingDebts);
        
        debtService.recordPayment("A", "B", 2);
        
        verify(debtLedger).saveDebts(argThat(List::isEmpty));
    }
}