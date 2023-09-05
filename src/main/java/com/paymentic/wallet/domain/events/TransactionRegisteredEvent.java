package com.paymentic.wallet.domain.events;

import com.paymentic.wallet.domain.shared.BuyerInfo;
import com.paymentic.wallet.domain.shared.CheckoutId;
import com.paymentic.wallet.domain.shared.PaymentOrderId;
import com.paymentic.wallet.domain.shared.SellerInfo;
import com.paymentic.wallet.domain.shared.TransactionId;
import java.time.LocalDateTime;

public record TransactionRegisteredEvent(TransactionId transaction, SellerInfo seller,
                                         PaymentOrderId payment, CheckoutId checkout, BuyerInfo buyer,
                                         String amount, String currency, LocalDateTime at) {
  private static final String EVENT_TYPE = "paymentic.payments-gateway.v1.transaction-registered";
  private static final String SUBJECT = "transaction-registered";
  private static final String SOURCE_PATTERN = "/transactions/%s";
  public String type() {
    return EVENT_TYPE;
  }
  public String source() {
    return String.format(SOURCE_PATTERN,this.transaction.id().toString());
  }
  public String subject() {
    return SUBJECT;
  }

}
