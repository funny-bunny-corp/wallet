package com.paymentic.wallet.adapter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentic.wallet.domain.application.TransactionService;
import com.paymentic.wallet.domain.events.TransactionProcessedEvent;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentOrderApprovedBridge {
  private static final String PAYMENT_ORDER_APPROVED_EVENT_TYPE = "paymentic.payments.gateway.v1.payment.order.approved";
  private final TransactionService transactionService;
  private final ObjectMapper mapper;
  public PaymentOrderApprovedBridge(TransactionService transactionService, ObjectMapper mapper) {
    this.transactionService = transactionService;
    this.mapper = mapper;
  }
  @KafkaListener(id = "paymentOrderApproved",groupId = "wallet-group-id", topics = "payments")
  public void listen(CloudEvent message){
    if (PAYMENT_ORDER_APPROVED_EVENT_TYPE.equals(message.getType())){
      PojoCloudEventData<TransactionProcessedEvent> deserializedData = CloudEventUtils
          .mapData(message, PojoCloudEventDataMapper.from(mapper, TransactionProcessedEvent.class));
      var transaction = deserializedData.getValue();
    }
  }

}
