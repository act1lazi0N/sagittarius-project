package com.sagittarius.payment.adapter.grpc;

import com.sagittarius.grpc.payment.OpenWalletRequest;
import com.sagittarius.grpc.payment.OpenWalletResponse;
import com.sagittarius.grpc.payment.PaymentServiceGrpc;
import com.sagittarius.payment.application.service.BalanceService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class PaymentGrpcService extends PaymentServiceGrpc.PaymentServiceImplBase {
    private final BalanceService balanceService;

    @Override
    public void openWallet(OpenWalletRequest request, StreamObserver<OpenWalletResponse> responseObserver) {
        balanceService.openWallet(request.getCustomerId());
        responseObserver.onNext(OpenWalletResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Wallet created")
                .build());
        responseObserver.onCompleted();
    }
}
