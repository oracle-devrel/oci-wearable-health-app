package com.oracle.cloud.wearable.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Alert {
    private String deviceSerialNumber;
    private String username;
    private String alertID;
    private Date alertDateTime;
    private String notificationChannel;
    private Double threshold;
    private Double observedValue;
    private String alertingParameter;
    private Integer notificationFrequency;
    private String emergencyContactEmail;
    private String emergencyContactMobile;
}
