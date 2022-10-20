package com.oracle.cloud.wearable.tcp.client.config;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.CachingClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioClientConnectionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.SerializationUtils;

@Configuration
@EnableScheduling
@EnableIntegration
public class TcpClientConfig {
	private static final Logger LOG = LoggerFactory.getLogger(TcpClientConfig.class);

	@Value(value = "${tcp.server.address}")
	private String tcpServerAddress;

	@Value(value = "${tcp.server.port}")
	private int tcpServerPort;

	@Value(value = "${tcp.client.timeout}")
	private int tcpClientTimeout;

	@Value("${tcp.client.poolSize}")
	private int tcpClientPoolSize;

	@Value("${tcp.client.worker.core.pool.size}")
	private int tcpClientWorkerCorePoolSize;

	@Value("${tcp.client.worker.max.pool.size}")
	private int tcpClientWorkerMaxPoolSize;

	@Value("${tcp.client.worker.queue.capacity}")
	private int tcpClientWorkerQueueCapacity;

	@Bean
	public DirectChannel tcpClientChannel() {
		return MessageChannels.direct().get();
	}

	@Bean
	public IntegrationFlow tcpClientToServerFlow() {
		return IntegrationFlows.from("tcpClientChannel")
				.transform(SerializationUtils::serialize)
				.handle(Tcp.outboundGateway(clientConnectionFactory())
						.remoteTimeout(tcpClientTimeout))
				.get();
	}

	@Bean
	public DirectChannel tcpClientErrorChannel() {
		return MessageChannels.direct().get();
	}

	@Bean
	public IntegrationFlow tcpClientErrorChannelFlow() {
		return IntegrationFlows.from("tcpClientErrorChannel")
				.handle(new MessageHandler() {
					@Override
					public void handleMessage(Message<?> message) throws MessagingException {
						LOG.error(String.format("Error communicating with tcp server. Message sent: %s",
								message.getPayload()));
					}
				}).get();
	}

	public AbstractClientConnectionFactory clientConnectionFactory() {
		final TcpNioClientConnectionFactory tcpNioClientConnectionFactory = new TcpNioClientConnectionFactory(
				tcpServerAddress, tcpServerPort);

		tcpNioClientConnectionFactory.setUsingDirectBuffers(true);
		tcpNioClientConnectionFactory.setSingleUse(false);
		tcpNioClientConnectionFactory.setSoKeepAlive(true);
		tcpNioClientConnectionFactory.setSoTimeout(10000);
		tcpNioClientConnectionFactory.setSoLinger(3000);
		return new CachingClientConnectionFactory(tcpNioClientConnectionFactory, tcpClientPoolSize);
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(tcpClientWorkerCorePoolSize);
		executor.setMaxPoolSize(tcpClientWorkerMaxPoolSize);
		executor.setQueueCapacity(tcpClientWorkerQueueCapacity);
		executor.setThreadNamePrefix("EventProducerClientPool-");
		executor.initialize();
		return executor;
	}
}