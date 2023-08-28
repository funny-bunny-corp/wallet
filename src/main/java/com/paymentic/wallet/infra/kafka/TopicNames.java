package com.paymentic.wallet.infra.kafka;

public enum TopicNames {
  PAYMENTS("payments");
  private final String name;

  TopicNames(String name) {
    this.name = name;
  }

  public String topicName(){
    return this.name;
  }

}
