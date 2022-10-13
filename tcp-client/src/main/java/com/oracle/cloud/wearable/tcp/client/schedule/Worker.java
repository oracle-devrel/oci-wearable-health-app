package com.oracle.cloud.wearable.tcp.client.schedule;

import org.apache.commons.lang3.RandomUtils;

import com.oracle.cloud.wearable.tcp.client.TcpClientGateway;
import com.oracle.cloud.wearable.tcp.model.RawRequest;

public class Worker implements Runnable {

    private static final String DEFAULT_DELIMITER = ";";
    private static final String BP_DELIMITER = ":";
    private static final String DEFAULT_EVENT_TYPE = "READING";

    private TcpClientGateway messageService;
    private String deviceSerialNumber;
    private Boolean abnormalReading;

    public Worker(TcpClientGateway messageService, String deviceSerialNumber, Boolean abnormalReading) {
        this.messageService = messageService;
        this.deviceSerialNumber = deviceSerialNumber;
        this.abnormalReading = abnormalReading;
    }

    public void run() {
        final RawRequest request = new RawRequest(createPayload());
        messageService.send(request);
    }

    private String createPayload() {
        final StringBuilder builder = new StringBuilder();
        builder.append(deviceSerialNumber);
        builder.append(DEFAULT_DELIMITER);
        builder.append(DEFAULT_EVENT_TYPE);
        builder.append(DEFAULT_DELIMITER);

        if (abnormalReading) {
            builder.append(RandomUtils.nextInt(40, 60));
        } else {
            builder.append(RandomUtils.nextInt(72, 120));
        }

        builder.append(DEFAULT_DELIMITER);

        if (abnormalReading) {
            builder.append(RandomUtils.nextInt(150, 250) + BP_DELIMITER + RandomUtils.nextInt(100, 130));
        } else {
            builder.append(RandomUtils.nextInt(100, 150) + BP_DELIMITER + RandomUtils.nextInt(50, 90));
        }

        builder.append(DEFAULT_DELIMITER);

        if (abnormalReading) {
            builder.append(RandomUtils.nextDouble(60, 80));
        } else {
            builder.append(RandomUtils.nextDouble(90, 100));
        }

        builder.append(DEFAULT_DELIMITER);
        builder.append(System.currentTimeMillis());
        return builder.toString();
    }
}