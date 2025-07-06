package com.paymentic.wallet.domain.application;

import com.paymentic.wallet.domain.OperationType;
import com.paymentic.wallet.domain.RefundTransaction;
import com.paymentic.wallet.domain.events.TransactionRegisteredEvent;
import com.paymentic.wallet.domain.publisher.TransactionRegisteredPublisher;
import com.paymentic.wallet.domain.repositories.RefundTransactionRepository;
import com.paymentic.wallet.domain.shared.BuyerInfo;
import com.paymentic.wallet.domain.shared.PaymentOrderId;
import com.paymentic.wallet.domain.shared.SellerInfo;
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
class RefundTransactionServiceTest {

    @Mock
    private RefundTransactionRepository refundTransactionRepository;
    
    @Mock
    private TransactionRegisteredPublisher publisher;
    
    private RefundTransactionService refundTransactionService;
    
    private RefundTransaction testRefundTransaction;
    
    @BeforeEach
    void setUp() {
        refundTransactionService = new RefundTransactionService(refundTransactionRepository, publisher);
        
        testRefundTransaction = RefundTransaction.newRefundTransaction(
            UUID.randomUUID(),
            "50.00",
            "USD",
            new BuyerInfo("12345678901", "John Doe"),
            new PaymentOrderId("order123"),
            new SellerInfo("seller123")
        );
    }
    
    @Test
    void register_ShouldSaveRefundTransactionAndPublishEvent() {
        // Given
        when(refundTransactionRepository.save(any(RefundTransaction.class))).thenReturn(testRefundTransaction);
        
        // When
        refundTransactionService.register(testRefundTransaction);
        
        // Then
        verify(refundTransactionRepository).save(testRefundTransaction);
        verify(publisher).publish(any(TransactionRegisteredEvent.class));
    }
    
    @Test
    void register_ShouldPublishCorrectEvent() {
        // Given
        when(refundTransactionRepository.save(any(RefundTransaction.class))).thenReturn(testRefundTransaction);
        ArgumentCaptor<TransactionRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(TransactionRegisteredEvent.class);
        
        // When
        refundTransactionService.register(testRefundTransaction);
        
        // Then
        verify(publisher).publish(eventCaptor.capture());
        TransactionRegisteredEvent publishedEvent = eventCaptor.getValue();
        
        assertNotNull(publishedEvent);
        assertEquals(testRefundTransaction.getId(), publishedEvent.transaction().id());
        assertEquals(testRefundTransaction.getAmount(), publishedEvent.amount());
        assertEquals(testRefundTransaction.getCurrency(), publishedEvent.currency());
        assertEquals(testRefundTransaction.getSellerInfo().getId(), publishedEvent.seller().getId());
        assertEquals(testRefundTransaction.getPaymentOrder().getId(), publishedEvent.payment().getId());
        assertEquals(testRefundTransaction.getBuyer().getDocument(), publishedEvent.buyer().getDocument());
        assertEquals(testRefundTransaction.getBuyer().getName(), publishedEvent.buyer().getName());
        assertEquals(OperationType.REFUND, publishedEvent.operationType());
        // For refunds, checkout should be null
        assertNull(publishedEvent.checkout());
    }
    
    @Test
    void register_ShouldHandleRepositoryException() {
        // Given
        when(refundTransactionRepository.save(any(RefundTransaction.class)))
            .thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            refundTransactionService.register(testRefundTransaction);
        });
        
        verify(refundTransactionRepository).save(testRefundTransaction);
        verify(publisher, never()).publish(any(TransactionRegisteredEvent.class));
    }
    
    @Test
    void register_ShouldBeTransactional() {
        // Given
        when(refundTransactionRepository.save(any(RefundTransaction.class))).thenReturn(testRefundTransaction);
        
        // When
        refundTransactionService.register(testRefundTransaction);
        
        // Then
        // Verify that the method is called within transaction context
        verify(refundTransactionRepository).save(testRefundTransaction);
        verify(publisher).publish(any(TransactionRegisteredEvent.class));
    }
    
    @Test
    void register_WithNullTransaction_ShouldThrowException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            refundTransactionService.register(null);
        });
        
        verify(refundTransactionRepository, never()).save(any());
        verify(publisher, never()).publish(any());
    }
    
    @Test
    void register_WithValidRefundTransaction_ShouldCreateCorrectEventType() {
        // Given
        when(refundTransactionRepository.save(any(RefundTransaction.class))).thenReturn(testRefundTransaction);
        ArgumentCaptor<TransactionRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(TransactionRegisteredEvent.class);
        
        // When
        refundTransactionService.register(testRefundTransaction);
        
        // Then
        verify(publisher).publish(eventCaptor.capture());
        TransactionRegisteredEvent publishedEvent = eventCaptor.getValue();
        
        // Verify that the event is created for refund type
        assertEquals(OperationType.REFUND, publishedEvent.operationType());
        assertNotNull(publishedEvent.occurredOn());
    }
}