package com.paymentic.wallet.domain.events;

import static java.util.Objects.requireNonNull;

import com.paymentic.wallet.domain.OperationType;
import com.paymentic.wallet.domain.shared.BuyerInfo;
import com.paymentic.wallet.domain.shared.CheckoutId;
import com.paymentic.wallet.domain.shared.PaymentOrderId;
import com.paymentic.wallet.domain.shared.SellerInfo;
import com.paymentic.wallet.domain.shared.TransactionId;
import java.time.LocalDateTime;

public class TransactionRegisteredEvent {
  private final TransactionId transaction;
  private final SellerInfo seller;
  private final PaymentOrderId payment;
  private CheckoutId checkout;
  private final BuyerInfo buyer;
  private final String amount;
  private final String currency;
  private final LocalDateTime at;
  private final OperationType operationType;
  private static final String EVENT_TYPE = "paymentic.payments-gateway.v1.transaction-registered";
  private static final String SOURCE_PATTERN = "/transactions/%s";
  TransactionRegisteredEvent(TransactionId transaction, SellerInfo seller,
      PaymentOrderId payment, CheckoutId checkout, BuyerInfo buyer,
      String amount, String currency, LocalDateTime at,
      OperationType operationType) {
    this.transaction = requireNonNull(transaction);
    this.seller = requireNonNull(seller);
    this.payment = requireNonNull(payment);
    this.checkout = requireNonNull(checkout);
    this.buyer = requireNonNull(buyer);
    this.amount = requireNonNull(amount);
    this.currency = requireNonNull(currency);
    this.at = requireNonNull(at);
    this.operationType = requireNonNull(operationType);
  }

  TransactionRegisteredEvent(TransactionId transaction, SellerInfo seller,
      PaymentOrderId payment, BuyerInfo buyer,
      String amount, String currency, LocalDateTime at,
      OperationType operationType) {
    this.transaction = requireNonNull(transaction);
    this.seller = requireNonNull(seller);
    this.payment = requireNonNull(payment);
    this.buyer = requireNonNull(buyer);
    this.amount = requireNonNull(amount);
    this.currency = requireNonNull(currency);
    this.at = requireNonNull(at);
    this.operationType = requireNonNull(operationType);
  }
  public static TransactionRegisteredEvent newTransactionWithCheckout(TransactionId transaction, SellerInfo seller,
      PaymentOrderId payment, CheckoutId checkout, BuyerInfo buyer,
      String amount, String currency, LocalDateTime at,
      OperationType operationType) {
    return new TransactionRegisteredEvent(transaction, seller, payment, checkout, buyer, amount, currency, at, operationType);
  }

  public static TransactionRegisteredEvent newTransactionWithoutCheckout(TransactionId transaction, SellerInfo seller,
      PaymentOrderId payment,  BuyerInfo buyer,
      String amount, String currency, LocalDateTime at,
      OperationType operationType) {
    return new TransactionRegisteredEvent(transaction, seller, payment, buyer, amount, currency, at, operationType);
  }
  public String type() {
    return EVENT_TYPE;
  }
  public String source() {
    return String.format(SOURCE_PATTERN, transaction.id().toString());
  }
  public String subject() {
    return operationType.subject();
  }
  public TransactionId getTransaction() {
    return transaction;
  }
  public SellerInfo getSeller() {
    return seller;
  }
  public PaymentOrderId getPayment() {
    return payment;
  }

  public CheckoutId getCheckout() {
    return checkout;
  }

  public BuyerInfo getBuyer() {
    return buyer;
  }

  public String getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  public LocalDateTime getAt() {
    return at;
  }

  public OperationType getOperationType() {
    return operationType;
  }
}

