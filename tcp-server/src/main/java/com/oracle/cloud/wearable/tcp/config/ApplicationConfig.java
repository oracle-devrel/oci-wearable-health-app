package com.oracle.cloud.wearable.tcp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpConnectionCloseEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionOpenEvent;
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/** */
@Configuration
@EnableScheduling
@Slf4j
public class ApplicationConfig {

  @Value("${tcp.server.port}")
  private Integer port;

  @Value("${tcp.message.payload.max.size}")
  private Integer maxPayloadSize;

  private static final AtomicInteger CONNECTION_COUNT = new AtomicInteger();

  @Bean
  public AbstractServerConnectionFactory serverConnectionFactory() {
    final TcpNioServerConnectionFactory serverConnectionFactory =
        new TcpNioServerConnectionFactory(port);
    serverConnectionFactory.setUsingDirectBuffers(true);
    serverConnectionFactory.setBacklog(1000);
    serverConnectionFactory.setSoTimeout(10000);
    serverConnectionFactory.setSoLinger(3000);
    serverConnectionFactory.setSoKeepAlive(true);
    serverConnectionFactory.setLookupHost(false);
    serverConnectionFactory.setSingleUse(false);
    serverConnectionFactory.setSerializer(codec());
    serverConnectionFactory.setDeserializer(codec());
    return serverConnectionFactory;
  }

  @Bean
  public MessageChannel inboundChannel() {
    return new DirectChannel();
  }

  @Bean
  public TcpInboundGateway inboundGateway() {
    final TcpInboundGateway tcpInboundGateway = new TcpInboundGateway();
    tcpInboundGateway.setConnectionFactory(serverConnectionFactory());
    tcpInboundGateway.setRequestChannel(inboundChannel());
    return tcpInboundGateway;
  }

  private ByteArrayCrLfSerializer codec() {
    final ByteArrayCrLfSerializer crLfSerializer = new ByteArrayCrLfSerializer();
    crLfSerializer.setMaxMessageSize(maxPayloadSize);
    return crLfSerializer;
  }

  @Bean
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(2);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("TCPMessageHandlerPool-");
    executor.initialize();
    return executor;
  }

  @EventListener
  public void listen(final TcpConnectionOpenEvent event) {
    log.debug("New connection with id {} opened ", event.getConnectionId());
    log.info("Total open connections {}", CONNECTION_COUNT.addAndGet(1));
  }

  @EventListener
  public void listen(final TcpConnectionCloseEvent event) {
    log.debug("Existing connection with id {} closed ", event.getConnectionId());
    log.info("Total open connections {}", CONNECTION_COUNT.addAndGet(-1));
  }
}