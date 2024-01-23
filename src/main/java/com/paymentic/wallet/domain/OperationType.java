package com.paymentic.wallet.domain;

public enum OperationType {
  PAYMENT("payment-created"),
  REFUND("refund-created");
  private final String eventSubject;
  OperationType(String eventSubject) {
    this.eventSubject = eventSubject;
  }
  public String subject() {
    return eventSubject;
  }

}
