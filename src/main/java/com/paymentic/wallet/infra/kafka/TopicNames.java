package com.paymentic.wallet.infra.kafka;

public enum TopicNames {
  PAYMENT_PROCESSING("payment-processing"),
  MERCHANT_ACCOUNT("merchant-account");
  private final String name;
  TopicNames(String name) {
    this.name = name;
  }
  public String topicName(){
    return this.name;
  }

}
