package com.paymentic.wallet.adapter.kafka.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentic.wallet.domain.PaymentTransaction;
import com.paymentic.wallet.domain.application.PaymentTransactionService;
import com.paymentic.wallet.domain.events.PaymentTransactionProcessedEvent;
import com.paymentic.wallet.infra.events.Event;
import com.paymentic.wallet.infra.events.service.EventService;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentOrderApprovedBridge {
  private static final String PAYMENT_ORDER_APPROVED_EVENT_TYPE = "paymentic.io.payment-processing.v1.payment-order.approved";
  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentOrderApprovedBridge.class);
  private static final String ERROR = "Event %s already handled!!!";
  private final PaymentTransactionService paymentTransactionService;
  private final ObjectMapper mapper;
  private final EventService eventService;
  public PaymentOrderApprovedBridge(PaymentTransactionService paymentTransactionService, ObjectMapper mapper,
      EventService eventService) {
    this.paymentTransactionService = paymentTransactionService;
    this.mapper = mapper;
    this.eventService = eventService;
  }
  @KafkaListener(id = "paymentOrderApproved",groupId = "wallet-group-id", topics = "payments")
  public void listen(CloudEvent message){
    var handle = this.eventService.shouldHandle(new Event(UUID.fromString(message.getId())));
    if (handle){
      if (PAYMENT_ORDER_APPROVED_EVENT_TYPE.equals(message.getType())){
        PojoCloudEventData<PaymentTransactionProcessedEvent> deserializedData = CloudEventUtils
            .mapData(message, PojoCloudEventDataMapper.from(mapper, PaymentTransactionProcessedEvent.class));
        var payload = deserializedData.getValue();
        var transaction = PaymentTransaction.newPaymentTransactionRegistered(payload.transaction().id(), payload.amount(),payload.currency(),payload.checkoutId(),payload.buyer(),payload.payment(),payload.seller());
        this.paymentTransactionService.register(transaction);
      }
    }else{
      LOGGER.error(String.format(ERROR, message.getId()));
    }

  }

}
