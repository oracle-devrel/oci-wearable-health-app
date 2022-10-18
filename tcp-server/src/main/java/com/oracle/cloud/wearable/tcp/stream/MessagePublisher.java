package com.oracle.cloud.wearable.tcp.stream;

import com.oracle.bmc.streaming.StreamClient;
import com.oracle.bmc.streaming.model.PutMessagesDetails;
import com.oracle.bmc.streaming.model.PutMessagesDetailsEntry;
import com.oracle.bmc.streaming.model.PutMessagesResultEntry;
import com.oracle.bmc.streaming.requests.PutMessagesRequest;
import com.oracle.bmc.streaming.responses.PutMessagesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MessagePublisher {

  private static final String ENV_KEY_STREAM_ID = "OCI_STREAM_OCID";

  @Autowired
  private MessageStreamingClient streamingClient;

  public void publishMessages(final List<String> messageList) {
    final StreamClient streamClient = streamingClient.getStreamClient();
    final List<PutMessagesDetailsEntry> messages = new ArrayList<>();

    messageList.forEach(message -> {
      messages.add(PutMessagesDetailsEntry.builder().value(message.getBytes()).build());
    });

    final PutMessagesDetails messagesDetails =
        PutMessagesDetails.builder().messages(messages).build();

    final String streamId = System.getenv().get(ENV_KEY_STREAM_ID);
    final PutMessagesRequest putRequest =
        PutMessagesRequest.builder().streamId(streamId).putMessagesDetails(messagesDetails).build();

    final PutMessagesResponse putResponse = streamClient.putMessages(putRequest);

    // the putResponse can contain some useful metadata for handling failures
    for (final PutMessagesResultEntry entry : putResponse.getPutMessagesResult().getEntries()) {
      if (StringUtils.hasText(entry.getError())) {
        log.warn("Error {}, {}", entry.getError(), entry.getErrorMessage());
      } else {
        log.debug("Published message to partition {}, offset {}.", entry.getPartition(), entry.getOffset());
      }
    }
  }
}