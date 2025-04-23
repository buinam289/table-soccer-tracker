package com.example.tablesoccer.controller;

import com.example.tablesoccer.model.Debt;
import com.example.tablesoccer.model.DebtResponse;
import com.example.tablesoccer.service.DebtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DebtController.class)
class DebtControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DebtService debtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllDebts() throws Exception {
        when(debtService.getAllDebts()).thenReturn(Arrays.asList(
            new Debt("A", "B", 2),
            new Debt("C", "A", 1)
        ));

        mockMvc.perform(get("/debts/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].from").value("A"))
                .andExpect(jsonPath("$[0].to").value("B"))
                .andExpect(jsonPath("$[0].amount").value(2))
                .andExpect(jsonPath("$[1].from").value("C"))
                .andExpect(jsonPath("$[1].to").value("A"))
                .andExpect(jsonPath("$[1].amount").value(1));
    }

    @Test
    void testGetPlayerDebts() throws Exception {
        DebtResponse response = new DebtResponse(
            Arrays.asList(new Debt("A", "B", 2)),
            Arrays.asList(new Debt("C", "A", 1))
        );

        when(debtService.getPlayerDebts("A")).thenReturn(response);

        mockMvc.perform(get("/debts").param("player", "A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owes[0].to").value("B"))
                .andExpect(jsonPath("$.owes[0].amount").value(2))
                .andExpect(jsonPath("$.owedBy[0].from").value("C"))
                .andExpect(jsonPath("$.owedBy[0].amount").value(1));
    }

    @Test
    void testRecordPayment() throws Exception {
        Map<String, Object> payment = new HashMap<>();
        payment.put("from", "A");
        payment.put("to", "B");
        payment.put("amount", 1);

        mockMvc.perform(post("/debts/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isOk());
    }

    @Test
    void testRecordPaymentInvalidRequest() throws Exception {
        Map<String, Object> payment = new HashMap<>(); // Empty payment
        
        mockMvc.perform(post("/debts/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isBadRequest());
    }
}