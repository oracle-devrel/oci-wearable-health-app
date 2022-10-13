package com.oracle.cloud.wearable.notification.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class QueueConsumerScheduler {

    @Autowired
    private MessageConsumer messageConsumer;

    @Scheduled(fixedDelay = 500L)
    public void consumeMessages() {
        messageConsumer.processMessages();
    }
}