package com.oracle.cloud.wearable.tcp.stream;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Scope("singleton")
public final class MessageBuffer {

    private final List<String> messages;

    MessageBuffer() {
        messages = new ArrayList<>();
    }

    public synchronized void addMessageToBuffer(final String message) {
        messages.add(message);
    }

    synchronized List<String> getMessages() {
        final List<String> messagesCopy = new ArrayList<>(messages.size());
        messages.forEach(msg -> {
            messagesCopy.add(String.valueOf(msg));
        });
        messages.clear();
        return messagesCopy;
    }

    @SuppressWarnings("unused")
    public void setMessages(final List<String> msgs) throws OperationNotSupportedException {
        throw new OperationNotSupportedException("This operation is not supported");
    }
}