package com.oracle.cloud.wearable.tcp.client.schedule;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.ansi.AnsiOutput;

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

        Integer abnormalParamCount = 0;

        if (abnormalReading) {
            abnormalParamCount = RandomUtils.nextInt(1, 5);
        }

        if (abnormalReading && abnormalParamCount > 0) {
            builder.append(RandomUtils.nextInt(121, 180));
            abnormalParamCount--;
        } else {
            builder.append(RandomUtils.nextInt(72, 120));
        }

        builder.append(DEFAULT_DELIMITER);

        if (abnormalReading && abnormalParamCount > 0) {
            builder.append(RandomUtils.nextInt(121, 250));
            abnormalParamCount--;
        } else {
            builder.append(RandomUtils.nextInt(100, 120));
        }

        builder.append(BP_DELIMITER);

        if (abnormalReading && abnormalParamCount > 0) {
            builder.append(RandomUtils.nextInt(60, 80));
            abnormalParamCount--;
        } else {
            builder.append(RandomUtils.nextInt(80, 90));
        }

        builder.append(DEFAULT_DELIMITER);

        if (abnormalReading && abnormalParamCount > 0) {
            builder.append(RandomUtils.nextDouble(60, 90));
            abnormalParamCount--;
        } else {
            builder.append(RandomUtils.nextDouble(90, 100));
        }

        builder.append(DEFAULT_DELIMITER);
        builder.append(System.currentTimeMillis());
        return builder.toString();
    }
}