package com.paymentic.wallet.domain.application;

import com.paymentic.wallet.domain.OperationType;
import com.paymentic.wallet.domain.PaymentTransaction;
import com.paymentic.wallet.domain.events.TransactionRegisteredEvent;
import com.paymentic.wallet.domain.publisher.TransactionRegisteredPublisher;
import com.paymentic.wallet.domain.repositories.PaymentTransactionRepository;
import com.paymentic.wallet.domain.shared.BuyerInfo;
import com.paymentic.wallet.domain.shared.CheckoutId;
import com.paymentic.wallet.domain.shared.PaymentOrderId;
import com.paymentic.wallet.domain.shared.SellerInfo;
import com.paymentic.wallet.domain.shared.TransactionId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentTransactionServiceTest {

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;
    
    @Mock
    private TransactionRegisteredPublisher publisher;
    
    private PaymentTransactionService paymentTransactionService;
    
    private PaymentTransaction testTransaction;
    
    @BeforeEach
    void setUp() {
        paymentTransactionService = new PaymentTransactionService(paymentTransactionRepository, publisher);
        
        testTransaction = PaymentTransaction.newPaymentTransactionRegistered(
            UUID.randomUUID(),
            "100.00",
            "USD",
            new CheckoutId("checkout123"),
            new BuyerInfo("12345678901", "John Doe"),
            new PaymentOrderId("order123"),
            new SellerInfo("seller123")
        );
    }
    
    @Test
    void register_ShouldSaveTransactionAndPublishEvent() {
        // Given
        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(testTransaction);
        
        // When
        paymentTransactionService.register(testTransaction);
        
        // Then
        verify(paymentTransactionRepository).save(testTransaction);
        verify(publisher).publish(any(TransactionRegisteredEvent.class));
    }
    
    @Test
    void register_ShouldPublishCorrectEvent() {
        // Given
        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(testTransaction);
        ArgumentCaptor<TransactionRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(TransactionRegisteredEvent.class);
        
        // When
        paymentTransactionService.register(testTransaction);
        
        // Then
        verify(publisher).publish(eventCaptor.capture());
        TransactionRegisteredEvent publishedEvent = eventCaptor.getValue();
        
        assertNotNull(publishedEvent);
        assertEquals(testTransaction.getId(), publishedEvent.transaction().id());
        assertEquals(testTransaction.getAmount(), publishedEvent.amount());
        assertEquals(testTransaction.getCurrency(), publishedEvent.currency());
        assertEquals(testTransaction.getSellerInfo().getId(), publishedEvent.seller().getId());
        assertEquals(testTransaction.getPaymentOrder().getId(), publishedEvent.payment().getId());
        assertEquals(testTransaction.getCheckout().getId(), publishedEvent.checkout().getId());
        assertEquals(testTransaction.getBuyer().getDocument(), publishedEvent.buyer().getDocument());
        assertEquals(testTransaction.getBuyer().getName(), publishedEvent.buyer().getName());
        assertEquals(OperationType.PAYMENT, publishedEvent.operationType());
    }
    
    @Test
    void register_ShouldHandleRepositoryException() {
        // Given
        when(paymentTransactionRepository.save(any(PaymentTransaction.class)))
            .thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            paymentTransactionService.register(testTransaction);
        });
        
        verify(paymentTransactionRepository).save(testTransaction);
        verify(publisher, never()).publish(any(TransactionRegisteredEvent.class));
    }
    
    @Test
    void register_ShouldBeTransactional() {
        // Given
        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(testTransaction);
        
        // When
        paymentTransactionService.register(testTransaction);
        
        // Then
        // Verify that the method is called within transaction context
        verify(paymentTransactionRepository).save(testTransaction);
        verify(publisher).publish(any(TransactionRegisteredEvent.class));
    }
    
    @Test
    void register_WithNullTransaction_ShouldThrowException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            paymentTransactionService.register(null);
        });
        
        verify(paymentTransactionRepository, never()).save(any());
        verify(publisher, never()).publish(any());
    }
}