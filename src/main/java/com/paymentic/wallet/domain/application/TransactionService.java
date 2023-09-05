package com.paymentic.wallet.domain.application;

import com.paymentic.wallet.domain.Transaction;
import com.paymentic.wallet.domain.events.TransactionRegisteredEvent;
import com.paymentic.wallet.domain.publisher.TransactionRegisteredPublisher;
import com.paymentic.wallet.domain.repositories.TransactionRepository;
import com.paymentic.wallet.domain.shared.TransactionId;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
  private final TransactionRepository transactionRepository;

  private final TransactionRegisteredPublisher publisher;
  public TransactionService( TransactionRepository transactionRepository,
      TransactionRegisteredPublisher publisher) {
    this.transactionRepository = transactionRepository;
    this.publisher = publisher;
  }
  @Transactional
  public void register(Transaction transaction){
    this.transactionRepository.save(transaction);
    var registered = new TransactionRegisteredEvent(new TransactionId(transaction.getId()),transaction.getSellerInfo(),transaction.getPaymentOrder(),transaction.getCheckout(),transaction.getBuyer(),transaction.getAmount(),transaction.getCurrency(),
        LocalDateTime.now());
    this.publisher.publish(registered);
  }

}
