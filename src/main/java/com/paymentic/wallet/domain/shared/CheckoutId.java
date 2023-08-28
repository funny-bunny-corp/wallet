package com.paymentic.wallet.domain.shared;

import jakarta.persistence.Embeddable;

@Embeddable
public class CheckoutId {
  private String id;
  public CheckoutId() {}
  public CheckoutId(String id) {
    this.id = id;
  }
  public String getId() {
    return id;
  }

}


