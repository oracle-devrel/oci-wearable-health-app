package com.oracle.cloud.wearable.tcp.server.endpoint;

import com.oracle.cloud.wearable.tcp.server.service.MessageHandlerService;
import com.oracle.cloud.wearable.tcp.server.service.impl.MessageHandlerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import java.util.concurrent.Executor;

/** */
@MessageEndpoint
public class Endpoint {

  private static final String ACK = "ACK";

  private final ApplicationContext ctx;

  private final Executor taskExecutor;

  @Autowired
  public Endpoint(final ApplicationContext ctx, final Executor taskExecutor) {
    this.ctx = ctx;
    this.taskExecutor = taskExecutor;
  }

  @ServiceActivator(inputChannel = "inboundChannel")
  public byte[] process(final byte[] message) {
    final MessageHandlerService service = ctx.getBean(MessageHandlerServiceImpl.class, message);
    taskExecutor.execute(service);
    return getResponse();
  }

  private byte[] getResponse() {
    return ACK.getBytes();
  }
}
