package com.paymentic.wallet.domain.repositories;

import com.paymentic.wallet.domain.Transaction;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, UUID> { }
