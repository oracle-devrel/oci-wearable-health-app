package com.oracle.cloud.wearable.notification.model.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 *
 */
@Entity
@Table(name = "user_alert_notifications")
@JsonInclude(JsonInclude.Include. NON_NULL)
@Data
public class UserAlertNotifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String alertId;
    private String alertParameter;
    private Double observedValue;
    private Date notificationTime;
    private String notificationChannel;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToOne
    @JoinColumn(name = "device_id", referencedColumnName = "id")
    private Device device;
}