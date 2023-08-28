package com.paymentic.wallet.domain.application;

import com.paymentic.wallet.domain.Transaction;
import com.paymentic.wallet.domain.repositories.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
  private final TransactionRepository transactionRepository;
  public TransactionService( TransactionRepository transactionRepository) {
    this.transactionRepository = transactionRepository;
  }
  @Transactional
  public void register(Transaction transaction){

  }


}
