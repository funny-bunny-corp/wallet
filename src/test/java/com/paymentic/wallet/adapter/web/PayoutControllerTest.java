package com.paymentic.wallet.adapter.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentic.wallet.adapter.web.dto.PayoutRequest;
import com.paymentic.wallet.adapter.web.dto.PayoutRequest.AmountDto;
import com.paymentic.wallet.adapter.web.dto.PayoutRequest.SellerDto;
import com.paymentic.wallet.domain.PaymentTransaction;
import com.paymentic.wallet.domain.application.PaymentTransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PayoutController.class)
class PayoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentTransactionService paymentTransactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPayout_WithValidRequest_ShouldReturnCreatedStatus() throws Exception {
        // Given
        PayoutRequest request = new PayoutRequest(
            "Thanks for your patronage!",
            new AmountDto("USD", "9.87"),
            new SellerDto("28c80b82-3917-11ee-b450-325096b39f47")
        );

        // When & Then
        mockMvc.perform(post("/payouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PROCESSED"))
                .andExpect(jsonPath("$.note").value("Thanks for your patronage!"))
                .andExpect(jsonPath("$.transactionId").exists());

        verify(paymentTransactionService).register(any(PaymentTransaction.class));
    }

    @Test
    void createPayout_WithoutNote_ShouldReturnCreatedStatus() throws Exception {
        // Given
        PayoutRequest request = new PayoutRequest(
            null,
            new AmountDto("USD", "9.87"),
            new SellerDto("28c80b82-3917-11ee-b450-325096b39f47")
        );

        // When & Then
        mockMvc.perform(post("/payouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PROCESSED"))
                .andExpect(jsonPath("$.note").isEmpty())
                .andExpect(jsonPath("$.transactionId").exists());

        verify(paymentTransactionService).register(any(PaymentTransaction.class));
    }

    @Test
    void createPayout_WithoutAmount_ShouldReturnBadRequest() throws Exception {
        // Given
        PayoutRequest request = new PayoutRequest(
            "Thanks for your patronage!",
            null,
            new SellerDto("28c80b82-3917-11ee-b450-325096b39f47")
        );

        // When & Then
        mockMvc.perform(post("/payouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(paymentTransactionService, never()).register(any(PaymentTransaction.class));
    }

    @Test
    void createPayout_WithoutSeller_ShouldReturnBadRequest() throws Exception {
        // Given
        PayoutRequest request = new PayoutRequest(
            "Thanks for your patronage!",
            new AmountDto("USD", "9.87"),
            null
        );

        // When & Then
        mockMvc.perform(post("/payouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(paymentTransactionService, never()).register(any(PaymentTransaction.class));
    }

    @Test
    void createPayout_WithInvalidAmountCurrency_ShouldReturnBadRequest() throws Exception {
        // Given
        PayoutRequest request = new PayoutRequest(
            "Thanks for your patronage!",
            new AmountDto(null, "9.87"),
            new SellerDto("28c80b82-3917-11ee-b450-325096b39f47")
        );

        // When & Then
        mockMvc.perform(post("/payouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(paymentTransactionService, never()).register(any(PaymentTransaction.class));
    }

    @Test
    void createPayout_WithInvalidAmountValue_ShouldReturnBadRequest() throws Exception {
        // Given
        PayoutRequest request = new PayoutRequest(
            "Thanks for your patronage!",
            new AmountDto("USD", null),
            new SellerDto("28c80b82-3917-11ee-b450-325096b39f47")
        );

        // When & Then
        mockMvc.perform(post("/payouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(paymentTransactionService, never()).register(any(PaymentTransaction.class));
    }

    @Test
    void createPayout_WithInvalidSellerAccount_ShouldReturnBadRequest() throws Exception {
        // Given
        PayoutRequest request = new PayoutRequest(
            "Thanks for your patronage!",
            new AmountDto("USD", "9.87"),
            new SellerDto(null)
        );

        // When & Then
        mockMvc.perform(post("/payouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(paymentTransactionService, never()).register(any(PaymentTransaction.class));
    }

    @Test
    void createPayout_WithEmptyRequestBody_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/payouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        verify(paymentTransactionService, never()).register(any(PaymentTransaction.class));
    }

    @Test
    void createPayout_WithServiceException_ShouldReturnInternalServerError() throws Exception {
        // Given
        PayoutRequest request = new PayoutRequest(
            "Thanks for your patronage!",
            new AmountDto("USD", "9.87"),
            new SellerDto("28c80b82-3917-11ee-b450-325096b39f47")
        );

        doThrow(new RuntimeException("Service error")).when(paymentTransactionService).register(any(PaymentTransaction.class));

        // When & Then
        mockMvc.perform(post("/payouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(paymentTransactionService).register(any(PaymentTransaction.class));
    }

    @Test
    void createPayout_WithDifferentCurrency_ShouldReturnCreatedStatus() throws Exception {
        // Given
        PayoutRequest request = new PayoutRequest(
            "EUR payout",
            new AmountDto("EUR", "8.50"),
            new SellerDto("28c80b82-3917-11ee-b450-325096b39f47")
        );

        // When & Then
        mockMvc.perform(post("/payouts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PROCESSED"))
                .andExpect(jsonPath("$.note").value("EUR payout"))
                .andExpect(jsonPath("$.transactionId").exists());

        verify(paymentTransactionService).register(any(PaymentTransaction.class));
    }
}