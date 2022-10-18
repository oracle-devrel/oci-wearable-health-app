package com.oracle.cloud.wearable.notification.queue;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.queue.QueueAdminClient;
import com.oracle.bmc.queue.QueueClient;
import com.oracle.bmc.queue.requests.GetQueueRequest;
import com.oracle.bmc.queue.responses.GetQueueResponse;
import com.oracle.cloud.wearable.notification.util.AuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope("singleton")
public class QueueClientProvider {
  private static final String ENV_KEY_QUEUE_OCID = "OCI_QUEUE_OCID";
  private static final String ENV_KEY_QUEUE_REGION_ID = "OCI_QUEUE_REGION_ID";

  private volatile QueueClient queueClient;

  private AuthenticationProvider authenticationProvider;

  @Autowired
  QueueClientProvider(final AuthenticationProvider provider) {
    this.authenticationProvider = provider;
    initiateQueueClient();
  }

  QueueClient getQueueClient() {
    if (queueClient == null) {
      initiateQueueClient();
    }
    return queueClient;
  }

  private void initiateQueueClient() {
    if (queueClient == null) {
      synchronized (this) {
        if (queueClient == null) {
          final BasicAuthenticationDetailsProvider authProvider =
              authenticationProvider.getProvider();
          final String endpoint = getQueueEndpoint(authProvider);
          queueClient = QueueClient.builder().build(authProvider);
          log.info("Queue client created successfully");

          queueClient.setEndpoint(endpoint);
        }
      }
    }
  }

  private String getQueueEndpoint(BasicAuthenticationDetailsProvider authProvider) {
    final QueueAdminClient adminClient =
        QueueAdminClient.builder()
            .region(Region.fromRegionId(System.getenv().get(ENV_KEY_QUEUE_REGION_ID)))
            .build(authProvider);
    final GetQueueResponse getResponse =
        adminClient.getQueue(
            GetQueueRequest.builder().queueId(System.getenv().get(ENV_KEY_QUEUE_OCID)).build());
    return getResponse.getQueue().getMessagesEndpoint();
  }
}