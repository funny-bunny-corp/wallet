package com.paymentic.wallet.domain.publisher;

import com.paymentic.wallet.domain.Transaction;

public interface TransactionRegisteredPublisher {
  void publish(Transaction transaction);

}
