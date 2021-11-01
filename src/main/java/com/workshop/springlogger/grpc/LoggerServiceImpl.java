package com.workshop.springlogger.grpc;


import com.workshop.springlogger.data.model.LogEntity;
import com.workshop.springlogger.proto.*;
import com.workshop.springlogger.proto.LoggerServiceGrpc;
import com.workshop.springlogger.service.LogService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDateTime;


@GrpcService
@RequiredArgsConstructor
public class LoggerServiceImpl extends LoggerServiceGrpc.LoggerServiceImplBase {

    private final LogService service;

    @Override
    public void log(LogRequest request, StreamObserver<LogResponse> responseObserver) {
        try {
            service.log(LogEntity.builder()
                    .logDate(LocalDateTime.now())
                    .logLevel(request.getLevel())
                    .logMessage(request.getMessage())
                    .applicationName(request.getApplicationName())
                    .build());
            LogResponse response = LogResponse.newBuilder()
                    .setResponseMessage(request.getMessage())
                    .setSuccess(true)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception ex) {

            LogResponse response = LogResponse.newBuilder()
                    .setResponseMessage(ex.getMessage())
                    .setSuccess(false)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onError(ex);
        }


    }
}