package com.paymentic.wallet.domain.publisher;

import com.paymentic.wallet.domain.events.TransactionRegisteredEvent;

public interface TransactionRegisteredPublisher {
  void publish(TransactionRegisteredEvent transaction);

}
