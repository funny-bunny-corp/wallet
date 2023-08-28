package com.paymentic.wallet.infra.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.kafka.CloudEventDeserializer;
import io.cloudevents.kafka.CloudEventSerializer;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfigProducer {
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, CloudEvent> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, CloudEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "wallet-group-id");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CloudEventDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    return props;
  }
  @Bean(name = "kafkaProducer")
  public ProducerFactory<String, CloudEvent> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfigs());
  }
  @Bean
  public ConsumerFactory<String, CloudEvent> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }
  @Bean
  public KafkaTemplate<String, CloudEvent> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  public Map<String, Object> producerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CloudEventSerializer.class);
    return props;
  }

}
