package com.paymentic.wallet.adapter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentic.wallet.adapter.kafka.in.PaymentOrderApprovedBridge;
import com.paymentic.wallet.domain.PaymentTransaction;
import com.paymentic.wallet.domain.application.PaymentTransactionService;
import com.paymentic.wallet.domain.events.PaymentTransactionProcessedEvent;
import com.paymentic.wallet.domain.shared.BuyerInfo;
import com.paymentic.wallet.domain.shared.CheckoutId;
import com.paymentic.wallet.domain.shared.PaymentOrderId;
import com.paymentic.wallet.domain.shared.SellerInfo;
import com.paymentic.wallet.domain.shared.TransactionId;
import com.paymentic.wallet.infra.events.Event;
import com.paymentic.wallet.infra.events.service.EventService;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import java.net.URI;
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
class PaymentOrderApprovedBridgeTest {

    @Mock
    private PaymentTransactionService paymentTransactionService;

    @Mock
    private EventService eventService;

    private ObjectMapper objectMapper;

    private PaymentOrderApprovedBridge paymentOrderApprovedBridge;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        paymentOrderApprovedBridge = new PaymentOrderApprovedBridge(
            paymentTransactionService, objectMapper, eventService);
    }

    @Test
    void listen_WithValidPaymentOrderApprovedEvent_ShouldProcessEvent() {
        // Given
        var eventId = UUID.randomUUID();
        var transactionId = UUID.randomUUID();
        var paymentProcessedEvent = new PaymentTransactionProcessedEvent(
            new TransactionId(transactionId),
            new SellerInfo("seller-123"),
            new PaymentOrderId("order-123"),
            new CheckoutId("checkout-123"),
            new BuyerInfo("buyer-doc", "buyer-name"),
            "100.00",
            "USD"
        );

        when(eventService.shouldHandle(any(Event.class))).thenReturn(true);

        var cloudEvent = CloudEventBuilder.v1()
            .withId(eventId.toString())
            .withSource(URI.create("test-source"))
            .withType("funny-bunny.xyz.payment-processing.v1.payment-order.approved")
            .withData(PojoCloudEventData.wrap(paymentProcessedEvent, objectMapper::writeValueAsBytes))
            .build();

        // When
        paymentOrderApprovedBridge.listen(cloudEvent);

        // Then
        verify(eventService).shouldHandle(any(Event.class));
        verify(paymentTransactionService).register(any(PaymentTransaction.class));
    }

    @Test
    void listen_WithAlreadyHandledEvent_ShouldNotProcessEvent() {
        // Given
        var eventId = UUID.randomUUID();
        var cloudEvent = CloudEventBuilder.v1()
            .withId(eventId.toString())
            .withSource(URI.create("test-source"))
            .withType("funny-bunny.xyz.payment-processing.v1.payment-order.approved")
            .withData("test-data")
            .build();

        when(eventService.shouldHandle(any(Event.class))).thenReturn(false);

        // When
        paymentOrderApprovedBridge.listen(cloudEvent);

        // Then
        verify(eventService).shouldHandle(any(Event.class));
        verify(paymentTransactionService, never()).register(any(PaymentTransaction.class));
    }

    @Test
    void listen_WithDifferentEventType_ShouldNotProcessEvent() {
        // Given
        var eventId = UUID.randomUUID();
        var cloudEvent = CloudEventBuilder.v1()
            .withId(eventId.toString())
            .withSource(URI.create("test-source"))
            .withType("different.event.type")
            .withData("test-data")
            .build();

        when(eventService.shouldHandle(any(Event.class))).thenReturn(true);

        // When
        paymentOrderApprovedBridge.listen(cloudEvent);

        // Then
        verify(eventService).shouldHandle(any(Event.class));
        verify(paymentTransactionService, never()).register(any(PaymentTransaction.class));
    }

    @Test
    void listen_WithValidEvent_ShouldCreateCorrectPaymentTransaction() {
        // Given
        var eventId = UUID.randomUUID();
        var transactionId = UUID.randomUUID();
        var paymentProcessedEvent = new PaymentTransactionProcessedEvent(
            new TransactionId(transactionId),
            new SellerInfo("seller-456"),
            new PaymentOrderId("order-456"),
            new CheckoutId("checkout-456"),
            new BuyerInfo("buyer-doc-123", "John Doe"),
            "250.75",
            "EUR"
        );

        when(eventService.shouldHandle(any(Event.class))).thenReturn(true);
        ArgumentCaptor<PaymentTransaction> transactionCaptor = ArgumentCaptor.forClass(PaymentTransaction.class);

        var cloudEvent = CloudEventBuilder.v1()
            .withId(eventId.toString())
            .withSource(URI.create("test-source"))
            .withType("funny-bunny.xyz.payment-processing.v1.payment-order.approved")
            .withData(PojoCloudEventData.wrap(paymentProcessedEvent, objectMapper::writeValueAsBytes))
            .build();

        // When
        paymentOrderApprovedBridge.listen(cloudEvent);

        // Then
        verify(paymentTransactionService).register(transactionCaptor.capture());
        
        PaymentTransaction capturedTransaction = transactionCaptor.getValue();
        assertNotNull(capturedTransaction);
        assertEquals(transactionId, capturedTransaction.getId());
        assertEquals("250.75", capturedTransaction.getAmount());
        assertEquals("EUR", capturedTransaction.getCurrency());
        assertEquals("seller-456", capturedTransaction.getSellerInfo().getId());
        assertEquals("order-456", capturedTransaction.getPaymentOrder().getId());
        assertEquals("checkout-456", capturedTransaction.getCheckout().getId());
        assertEquals("buyer-doc-123", capturedTransaction.getBuyer().getDocument());
        assertEquals("John Doe", capturedTransaction.getBuyer().getName());
    }

    @Test
    void listen_WithNullEvent_ShouldHandleGracefully() {
        // When & Then
        assertDoesNotThrow(() -> paymentOrderApprovedBridge.listen(null));
        verify(eventService, never()).shouldHandle(any(Event.class));
        verify(paymentTransactionService, never()).register(any(PaymentTransaction.class));
    }
}