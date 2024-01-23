package com.paymentic.wallet.domain.application;

import com.paymentic.wallet.domain.OperationType;
import com.paymentic.wallet.domain.RefundTransaction;
import com.paymentic.wallet.domain.events.TransactionRegisteredEvent;
import com.paymentic.wallet.domain.publisher.TransactionRegisteredPublisher;
import com.paymentic.wallet.domain.repositories.RefundTransactionRepository;
import com.paymentic.wallet.domain.shared.TransactionId;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefundTransactionService {
  private final RefundTransactionRepository refundTransactionRepository;

  private final TransactionRegisteredPublisher publisher;
  public RefundTransactionService(RefundTransactionRepository refundTransactionRepository,
      TransactionRegisteredPublisher publisher) {
    this.refundTransactionRepository = refundTransactionRepository;
    this.publisher = publisher;
  }
  @Transactional
  public void register(RefundTransaction refundTransaction){
    this.refundTransactionRepository.save(refundTransaction);
    var registered = TransactionRegisteredEvent.newTransactionWithoutCheckout(new TransactionId(refundTransaction.getId()),
        refundTransaction.getSellerInfo(), refundTransaction.getPaymentOrder(),
         refundTransaction.getBuyer(), refundTransaction.getAmount(),
        refundTransaction.getCurrency(),
        LocalDateTime.now(), OperationType.REFUND);
    this.publisher.publish(registered);
  }

}
