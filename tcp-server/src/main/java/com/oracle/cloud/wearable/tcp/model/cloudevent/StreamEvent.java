package com.oracle.cloud.wearable.tcp.model.cloudevent;

import com.oracle.cloud.wearable.tcp.util.EventType;
import lombok.Data;

@Data
public class StreamEvent {
    private String deviceSerialNumber;
    private String username;
    private Long userId;
    private Integer heartRate;
    private Integer systolicBP;
    private Integer diastolicBP;
    private Double spo2Level;
    private Long readingTime;
    private EventType eventType;
}