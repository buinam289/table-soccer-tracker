package com.example.tablesoccer.repository;

import com.example.tablesoccer.model.Debt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DebtLedgerTest {
    @TempDir
    Path tempDir;
    private DebtLedger debtLedger;

    @BeforeEach
    void setUp() throws IOException {
        System.setProperty("data/debts.json", tempDir.resolve("debts.json").toString());
        debtLedger = new DebtLedger();
    }

    @Test
    void testSaveAndGetDebts() throws IOException {
        List<Debt> debts = Arrays.asList(
            new Debt("A", "B", 2),
            new Debt("C", "A", 1)
        );
        
        debtLedger.saveDebts(debts);
        List<Debt> loadedDebts = debtLedger.getAllDebts();
        
        assertEquals(2, loadedDebts.size());
        assertTrue(loadedDebts.stream().anyMatch(d -> 
            d.getFrom().equals("A") && d.getTo().equals("B") && d.getAmount() == 2));
        assertTrue(loadedDebts.stream().anyMatch(d -> 
            d.getFrom().equals("C") && d.getTo().equals("A") && d.getAmount() == 1));
    }

    @Test
    void testDebtConsolidation() throws IOException {
        List<Debt> debts = Arrays.asList(
            new Debt("A", "B", 3),
            new Debt("B", "A", 1)
        );
        
        debtLedger.saveDebts(debts);
        List<Debt> consolidatedDebts = debtLedger.getAllDebts();
        
        assertEquals(1, consolidatedDebts.size());
        Debt consolidatedDebt = consolidatedDebts.get(0);
        assertEquals("A", consolidatedDebt.getFrom());
        assertEquals("B", consolidatedDebt.getTo());
        assertEquals(2, consolidatedDebt.getAmount());
    }
}
