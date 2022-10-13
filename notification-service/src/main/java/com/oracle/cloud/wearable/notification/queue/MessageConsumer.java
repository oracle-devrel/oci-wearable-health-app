package com.oracle.cloud.wearable.notification.queue;

import com.oracle.bmc.queue.model.DeleteMessagesDetails;
import com.oracle.bmc.queue.model.DeleteMessagesDetailsEntry;
import com.oracle.bmc.queue.model.GetMessage;
import com.oracle.bmc.queue.requests.DeleteMessagesRequest;
import com.oracle.bmc.queue.requests.GetMessagesRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/** */
@Slf4j
@Component
public class MessageConsumer {

  private static final String ENV_KEY_QUEUE_OCID = "OCI_QUEUE_OCID";

  private QueueClientProvider clientProvider;

  private Executor taskExecutor;

  private ApplicationContext ctx;

  @Autowired
  MessageConsumer(
      final QueueClientProvider qcp,
      @Qualifier("workerpool") final Executor exec,
      final ApplicationContext ac) {
    this.clientProvider = qcp;
    this.taskExecutor = exec;
    this.ctx = ac;
  }

  public void processMessages() {
    final List<GetMessage> messages =
        clientProvider
            .getQueueClient()
            .getMessages(buildGetMessageRequest())
            .getGetMessages()
            .getMessages();

    if (messages.size() > 0) {
      log.info("{} messages fetched", messages.size());

      messages.forEach(
          message -> {
            taskExecutor.execute(ctx.getBean(Worker.class, message.getContent()));
          });
      deleteMessages(messages);
    }
  }

  private GetMessagesRequest buildGetMessageRequest() {
    return GetMessagesRequest.builder()
        .queueId(System.getenv().get(ENV_KEY_QUEUE_OCID))
        .timeoutInSeconds(30)
        .limit(20)
        .build();
  }

  private void deleteMessages(final List<GetMessage> messages) {
    final List<DeleteMessagesDetailsEntry> entries = new ArrayList<>(messages.size());
    messages.forEach(
        message -> {
          entries.add(DeleteMessagesDetailsEntry.builder().receipt(message.getReceipt()).build());
        });
    clientProvider.getQueueClient().deleteMessages(getDeleteMessagesRequest(entries));
    log.info("{} messages deleted", messages.size());
  }

  private DeleteMessagesRequest getDeleteMessagesRequest(
      final List<DeleteMessagesDetailsEntry> entries) {
    return DeleteMessagesRequest.builder()
        .queueId(System.getenv().get(ENV_KEY_QUEUE_OCID))
        .deleteMessagesDetails(DeleteMessagesDetails.builder().entries(entries).build())
        .build();
  }
}
