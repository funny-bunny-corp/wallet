package com.paymentic.wallet.domain.repositories;

import com.paymentic.wallet.domain.PaymentTransaction;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface PaymentTransactionRepository extends CrudRepository<PaymentTransaction, UUID> { }
