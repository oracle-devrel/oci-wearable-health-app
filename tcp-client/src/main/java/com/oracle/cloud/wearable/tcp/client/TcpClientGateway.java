package com.oracle.cloud.wearable.tcp.client;

import org.springframework.integration.annotation.MessagingGateway;

import com.oracle.cloud.wearable.tcp.model.RawRequest;

@MessagingGateway(defaultRequestChannel = "tcpClientChannel", errorChannel = "tcpClientErrorChannel")
public interface TcpClientGateway {
    String send(RawRequest message);
}