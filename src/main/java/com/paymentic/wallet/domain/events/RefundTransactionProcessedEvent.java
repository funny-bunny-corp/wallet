package com.paymentic.wallet.domain.events;

import com.paymentic.wallet.domain.shared.BuyerInfo;
import com.paymentic.wallet.domain.shared.CheckoutId;
import com.paymentic.wallet.domain.shared.PaymentOrderId;
import com.paymentic.wallet.domain.shared.SellerInfo;
import com.paymentic.wallet.domain.shared.TransactionId;
import java.time.LocalDateTime;

public record RefundTransactionProcessedEvent(TransactionId transaction, SellerInfo seller,
                                              String amount, String currency,PaymentOrderId payment,
                                              LocalDateTime at, BuyerInfo buyer) {}
