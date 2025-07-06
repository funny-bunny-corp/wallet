package com.paymentic.wallet.adapter.web;

import com.paymentic.wallet.adapter.web.dto.PayoutRequest;
import com.paymentic.wallet.adapter.web.dto.PayoutResponse;
import com.paymentic.wallet.domain.PaymentTransaction;
import com.paymentic.wallet.domain.application.PaymentTransactionService;
import com.paymentic.wallet.domain.shared.BuyerInfo;
import com.paymentic.wallet.domain.shared.CheckoutId;
import com.paymentic.wallet.domain.shared.PaymentOrderId;
import com.paymentic.wallet.domain.shared.SellerInfo;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payouts")
public class PayoutController {

    private final PaymentTransactionService paymentTransactionService;

    public PayoutController(PaymentTransactionService paymentTransactionService) {
        this.paymentTransactionService = paymentTransactionService;
    }

    @PostMapping
    public ResponseEntity<PayoutResponse> createPayout(@Valid @RequestBody PayoutRequest request) {
        // Create a payment transaction based on the payout request
        var transactionId = UUID.randomUUID();
        var paymentTransaction = PaymentTransaction.newPaymentTransactionRegistered(
            transactionId,
            request.getAmount().getValue(),
            request.getAmount().getCurrency(),
            new CheckoutId(UUID.randomUUID().toString()),
            new BuyerInfo("system", "system"),
            new PaymentOrderId(UUID.randomUUID().toString()),
            new SellerInfo(request.getSeller().getAccount())
        );

        paymentTransactionService.register(paymentTransaction);

        var response = new PayoutResponse(transactionId.toString(), "PROCESSED", request.getNote());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}