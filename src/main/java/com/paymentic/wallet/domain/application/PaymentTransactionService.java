package com.paymentic.wallet.domain.application;

import com.paymentic.wallet.domain.OperationType;
import com.paymentic.wallet.domain.PaymentTransaction;
import com.paymentic.wallet.domain.events.TransactionRegisteredEvent;
import com.paymentic.wallet.domain.publisher.TransactionRegisteredPublisher;
import com.paymentic.wallet.domain.repositories.PaymentTransactionRepository;
import com.paymentic.wallet.domain.shared.TransactionId;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentTransactionService {
  private final PaymentTransactionRepository paymentTransactionRepository;
  private final TransactionRegisteredPublisher publisher;
  public PaymentTransactionService( PaymentTransactionRepository paymentTransactionRepository,
      TransactionRegisteredPublisher publisher) {
    this.paymentTransactionRepository = paymentTransactionRepository;
    this.publisher = publisher;
  }
  @Transactional
  public void register(PaymentTransaction paymentTransaction){
    this.paymentTransactionRepository.save(paymentTransaction);
    var registered = TransactionRegisteredEvent.newTransactionWithCheckout(new TransactionId(paymentTransaction.getId()),
        paymentTransaction.getSellerInfo(), paymentTransaction.getPaymentOrder(),
        paymentTransaction.getCheckout(), paymentTransaction.getBuyer(), paymentTransaction.getAmount(),
        paymentTransaction.getCurrency(),
        LocalDateTime.now(), OperationType.PAYMENT);
    this.publisher.publish(registered);
  }

}
