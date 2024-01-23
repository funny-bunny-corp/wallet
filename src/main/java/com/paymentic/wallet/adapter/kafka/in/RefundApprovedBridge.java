package com.paymentic.wallet.adapter.kafka.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentic.wallet.domain.RefundTransaction;
import com.paymentic.wallet.domain.application.RefundTransactionService;
import com.paymentic.wallet.domain.events.RefundTransactionProcessedEvent;
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
public class RefundApprovedBridge {
  private static final String REFUND_APPROVED_EVENT_TYPE = "paymentic.payments.gateway.v1.refund.approved";
  private static final Logger LOGGER = LoggerFactory.getLogger(RefundApprovedBridge.class);
  private static final String ERROR = "Event %s already handled!!!";
  private final RefundTransactionService refundTransactionService;
  private final ObjectMapper mapper;
  private final EventService eventService;
  public RefundApprovedBridge(RefundTransactionService refundTransactionService, ObjectMapper mapper,
      EventService eventService) {
    this.refundTransactionService = refundTransactionService;
    this.mapper = mapper;
    this.eventService = eventService;
  }
  @KafkaListener(id = "refundOrderApproved",groupId = "wallet-group-id", topics = "payments")
  public void listen(CloudEvent message){
    var handle = this.eventService.shouldHandle(new Event(UUID.fromString(message.getId())));
    if (handle){
      if (REFUND_APPROVED_EVENT_TYPE.equals(message.getType())){
        PojoCloudEventData<RefundTransactionProcessedEvent> deserializedData = CloudEventUtils
            .mapData(message, PojoCloudEventDataMapper.from(mapper, RefundTransactionProcessedEvent.class));
        var payload = deserializedData.getValue();
        var refund = RefundTransaction.newRefundTransaction(payload.transaction().id(), payload.amount(),payload.currency(),payload.buyer(),payload.payment(),payload.seller());
        this.refundTransactionService.register(refund);
      }
    }else{
      LOGGER.error(String.format(ERROR, message.getId()));
    }

  }

}
