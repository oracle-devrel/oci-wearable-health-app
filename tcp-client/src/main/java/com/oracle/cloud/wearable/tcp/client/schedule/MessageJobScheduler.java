package com.oracle.cloud.wearable.tcp.client.schedule;

import com.oracle.cloud.wearable.tcp.client.TcpClientGateway;
import com.oracle.cloud.wearable.tcp.client.util.PlaceHolder;
import com.oracle.cloud.wearable.tcp.model.RawRequest;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MessageJobScheduler {

    private TcpClientGateway messageService;
    private Executor taskExecutor;

    @Autowired
    public MessageJobScheduler(TcpClientGateway messageService, Executor taskExecutor) {
        this.messageService = messageService;
        this.taskExecutor = taskExecutor;
    }

    @Scheduled(fixedDelay = 10000L)
    public void sendMessageJob() {
        final Set<String> keys = PlaceHolder.DEVICE_SERIAL_NUMBERS.keySet();
        Integer counter = 1000;

        if (keys.size() < counter) {
            counter = keys.size();
        }

        Integer i = 1;
        for (final String key : keys) {
            final Worker worker = new Worker(messageService, key, PlaceHolder.DEVICE_SERIAL_NUMBERS.get(key));
            taskExecutor.execute(worker);
            if (i >= counter) {
                break;
            }
        }
    }
}