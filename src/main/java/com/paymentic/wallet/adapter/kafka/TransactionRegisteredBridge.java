package com.paymentic.wallet.adapter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentic.wallet.domain.Transaction;
import com.paymentic.wallet.domain.events.TransactionRegisteredEvent;
import com.paymentic.wallet.domain.publisher.TransactionRegisteredPublisher;
import com.paymentic.wallet.infra.ce.CExtensions;
import com.paymentic.wallet.infra.ce.CExtensions.Audience;
import com.paymentic.wallet.infra.ce.CExtensions.EventContext;
import com.paymentic.wallet.infra.kafka.TopicNames;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import java.net.URI;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionRegisteredBridge implements TransactionRegisteredPublisher {
  private final KafkaTemplate<String, CloudEvent> sender;

  private final ObjectMapper mapper;
  private final Logger logger = LoggerFactory.getLogger(TransactionRegisteredBridge.class);
  public TransactionRegisteredBridge(KafkaTemplate<String, CloudEvent> sender,
      ObjectMapper mapper) {
    this.sender = sender;
    this.mapper = mapper;
  }
  @Override
  public void publish(TransactionRegisteredEvent transaction) {
    var ce = CloudEventBuilder.v1()
        .withId(UUID.randomUUID().toString())
        .withSource(URI.create(transaction.source()))
        .withSubject(transaction.subject())
        .withType(transaction.type())
        .withData(PojoCloudEventData.wrap(transaction, mapper::writeValueAsBytes))
        .withExtension(CExtensions.AUDIENCE.extensionName(), Audience.INTERNAL_BOUNDED_CONTEXT.audienceName())
        .withExtension(CExtensions.EVENT_CONTEXT.extensionName(), EventContext.DOMAIN.eventContextName())
        .build();
    this.sender.send(TopicNames.PAYMENTS.topicName(), ce)
        .thenRun(() -> logger.info("Message sent. Id: {}; Data: {}", ce.getId(), transaction));
  }



}
