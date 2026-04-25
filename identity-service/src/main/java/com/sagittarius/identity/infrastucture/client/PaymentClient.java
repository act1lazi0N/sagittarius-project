package com.sagittarius.identity.infrastucture.client;

import com.sagittarius.common.exception.BusinessException;
import com.sagittarius.grpc.payment.OpenWalletRequest;
import com.sagittarius.grpc.payment.PaymentServiceGrpc;
import com.sagittarius.identity.application.exception.IdentityErrorCode;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@Slf4j
public class PaymentClient {
    @GrpcClient("payment-service")
    private PaymentServiceGrpc.PaymentServiceBlockingStub paymentGrpcStub;

    public void openWallet(String customerId) {
        try {
            paymentGrpcStub.openWallet(OpenWalletRequest.newBuilder().setCustomerId(customerId).build());
            log.info("Opened wallet for customer {} via gRPC", customerId);
        } catch (Exception e) {
            log.error("Failed to open wallet via gRPC for user {}", customerId, e);
            throw new BusinessException(IdentityErrorCode.OPEN_WALLET_FAILED);
        }
    }
}
