package com.paymentic.wallet.domain;

import com.paymentic.wallet.domain.shared.BuyerInfo;
import com.paymentic.wallet.domain.shared.CheckoutId;
import com.paymentic.wallet.domain.shared.PaymentOrderId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity(name = "seller_transaction")
public class Transaction {

  @Id
  @Column(name = "transaction_id")
  private UUID id;
  private String amount;
  private String currency;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name="id",column=@Column(name="checkout_id"))
  })
  private CheckoutId checkout;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name="document",column=@Column(name="buyer_document")),
      @AttributeOverride(name="name",column=@Column(name="buyer_name"))
  })
  private BuyerInfo buyer;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name="id",column=@Column(name="payment_order_id"))
  })
  private PaymentOrderId paymentOrder;
  public Transaction(){}

  public UUID getId() {
    return id;
  }

  public String getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  public CheckoutId getCheckout() {
    return checkout;
  }

  public BuyerInfo getBuyer() {
    return buyer;
  }

  public PaymentOrderId getPaymentOrder() {
    return paymentOrder;
  }
}
