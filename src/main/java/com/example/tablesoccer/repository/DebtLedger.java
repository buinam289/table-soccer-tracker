package com.example.tablesoccer.repository;

import com.example.tablesoccer.model.Debt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Repository
public class DebtLedger {
    private final String DATA_FILE = "data/debts.json";
    private final ObjectMapper objectMapper;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final File dataFile;

    public DebtLedger() throws IOException {
        this.objectMapper = new ObjectMapper();
        this.dataFile = ResourceUtils.getFile(DATA_FILE);
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            saveDebts(new ArrayList<>());
        }
    }

    public List<Debt> getAllDebts() throws IOException {
        lock.readLock().lock();
        try {
            if (!dataFile.exists()) {
                return new ArrayList<>();
            }
            List<Debt> debts = objectMapper.readValue(dataFile, new TypeReference<List<Debt>>() {});
            return consolidateDebts(debts);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void saveDebts(List<Debt> debts) throws IOException {
        lock.writeLock().lock();
        try {
            List<Debt> consolidatedDebts = consolidateDebts(debts);
            objectMapper.writeValue(dataFile, consolidatedDebts);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private List<Debt> consolidateDebts(List<Debt> debts) {
        Map<String, Map<String, Integer>> debtGraph = new HashMap<>();

        // Build debt graph
        for (Debt debt : debts) {
            debtGraph.computeIfAbsent(debt.getFrom(), k -> new HashMap<>());
            debtGraph.computeIfAbsent(debt.getTo(), k -> new HashMap<>());
            
            // Add debt amount
            debtGraph.get(debt.getFrom()).merge(debt.getTo(), debt.getAmount(), Integer::sum);
        }

        // Net out opposing debts
        List<Debt> consolidatedDebts = new ArrayList<>();
        Set<String> processed = new HashSet<>();

        for (Map.Entry<String, Map<String, Integer>> fromEntry : debtGraph.entrySet()) {
            String from = fromEntry.getKey();
            
            for (Map.Entry<String, Integer> toEntry : fromEntry.getValue().entrySet()) {
                String to = toEntry.getKey();
                
                if (processed.contains(from + to) || processed.contains(to + from)) {
                    continue;
                }

                int fromAmount = toEntry.getValue();
                int toAmount = debtGraph.get(to).getOrDefault(from, 0);
                
                if (fromAmount > toAmount) {
                    consolidatedDebts.add(new Debt(from, to, fromAmount - toAmount));
                } else if (toAmount > fromAmount) {
                    consolidatedDebts.add(new Debt(to, from, toAmount - fromAmount));
                }
                
                processed.add(from + to);
                processed.add(to + from);
            }
        }

        return consolidatedDebts.stream()
            .filter(debt -> debt.getAmount() > 0)
            .collect(Collectors.toList());
    }
}