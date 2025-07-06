package com.paymentic.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentic.wallet.adapter.web.dto.PayoutRequest;
import com.paymentic.wallet.adapter.web.dto.PayoutRequest.AmountDto;
import com.paymentic.wallet.adapter.web.dto.PayoutRequest.SellerDto;
import com.paymentic.wallet.domain.PaymentTransaction;
import com.paymentic.wallet.domain.repositories.PaymentTransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class WalletApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @MockBean
    private KafkaTemplate<String, ?> kafkaTemplate;

    @Test
    void createPayout_ShouldPersistTransactionAndReturnResponse() throws Exception {
        // Given
        PayoutRequest request = new PayoutRequest(
            "Integration test payout",
            new AmountDto("USD", "100.50"),
            new SellerDto("seller-123")
        );

        // When & Then
        var result = mockMvc.perform(post("/payouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PROCESSED"))
                .andExpect(jsonPath("$.note").value("Integration test payout"))
                .andExpect(jsonPath("$.transactionId").exists())
                .andReturn();

        // Verify transaction was persisted
        var response = result.getResponse().getContentAsString();
        var responseMap = objectMapper.readValue(response, java.util.Map.class);
        var transactionId = (String) responseMap.get("transactionId");
        
        assertNotNull(transactionId);
        
        // Verify the transaction exists in the database
        var persistedTransaction = paymentTransactionRepository.findById(java.util.UUID.fromString(transactionId));
        assertTrue(persistedTransaction.isPresent());
        
        PaymentTransaction transaction = persistedTransaction.get();
        assertEquals("100.50", transaction.getAmount());
        assertEquals("USD", transaction.getCurrency());
        assertEquals("seller-123", transaction.getSellerInfo().getId());
        assertEquals("system", transaction.getBuyer().getDocument());
        assertEquals("system", transaction.getBuyer().getName());
    }

    @Test
    void createPayout_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        PayoutRequest request = new PayoutRequest(
            "Invalid payout",
            new AmountDto(null, "100.50"), // Invalid - null currency
            new SellerDto("seller-123")
        );

        // When & Then
        mockMvc.perform(post("/payouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Verify no transaction was persisted
        var allTransactions = paymentTransactionRepository.findAll();
        assertTrue(!allTransactions.iterator().hasNext());
    }

    @Test
    void createPayout_WithEuroAmount_ShouldCreateTransactionSuccessfully() throws Exception {
        // Given
        PayoutRequest request = new PayoutRequest(
            "Euro payout",
            new AmountDto("EUR", "85.75"),
            new SellerDto("seller-456")
        );

        // When & Then
        var result = mockMvc.perform(post("/payouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PROCESSED"))
                .andExpect(jsonPath("$.note").value("Euro payout"))
                .andReturn();

        // Verify transaction details
        var response = result.getResponse().getContentAsString();
        var responseMap = objectMapper.readValue(response, java.util.Map.class);
        var transactionId = (String) responseMap.get("transactionId");
        
        var persistedTransaction = paymentTransactionRepository.findById(java.util.UUID.fromString(transactionId));
        assertTrue(persistedTransaction.isPresent());
        
        PaymentTransaction transaction = persistedTransaction.get();
        assertEquals("85.75", transaction.getAmount());
        assertEquals("EUR", transaction.getCurrency());
        assertEquals("seller-456", transaction.getSellerInfo().getId());
    }

    @Test
    void createPayout_WithoutNote_ShouldCreateTransactionSuccessfully() throws Exception {
        // Given
        PayoutRequest request = new PayoutRequest(
            null, // No note
            new AmountDto("GBP", "50.25"),
            new SellerDto("seller-789")
        );

        // When & Then
        var result = mockMvc.perform(post("/payouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PROCESSED"))
                .andExpect(jsonPath("$.note").isEmpty())
                .andReturn();

        // Verify transaction details
        var response = result.getResponse().getContentAsString();
        var responseMap = objectMapper.readValue(response, java.util.Map.class);
        var transactionId = (String) responseMap.get("transactionId");
        
        var persistedTransaction = paymentTransactionRepository.findById(java.util.UUID.fromString(transactionId));
        assertTrue(persistedTransaction.isPresent());
        
        PaymentTransaction transaction = persistedTransaction.get();
        assertEquals("50.25", transaction.getAmount());
        assertEquals("GBP", transaction.getCurrency());
        assertEquals("seller-789", transaction.getSellerInfo().getId());
    }

    @Test
    void applicationContext_ShouldLoadSuccessfully() {
        // This test verifies that the Spring Boot application context loads properly
        // with all the beans configured correctly
        assertNotNull(mockMvc);
        assertNotNull(objectMapper);
        assertNotNull(paymentTransactionRepository);
    }
}