package com.oracle.cloud.wearable.tcp.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 */
@Component
@Slf4j
public class BufferFlushingScheduler {

    @Autowired
    private MessagePublisher publisher;

    @Autowired
    private MessageBuffer messageBuffer;

    @Scheduled(fixedDelay = 3000L, initialDelay = 3000L)
    public void flushBuffer() {
        final List<String> messages = messageBuffer.getMessages();

        if(messages != null && !messages.isEmpty()) {
            log.info("Messages found in buffer {}", messages.size());
            publisher.publishMessages(messages);
        } else {
            log.debug("No Messages found in buffer");
        }
    }
}