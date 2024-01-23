package com.paymentic.wallet.domain.repositories;

import com.paymentic.wallet.domain.RefundTransaction;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface RefundTransactionRepository extends CrudRepository<RefundTransaction, UUID> { }
