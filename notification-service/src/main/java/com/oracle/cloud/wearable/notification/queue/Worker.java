package com.oracle.cloud.wearable.notification.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.cloud.wearable.notification.model.Alert;
import com.oracle.cloud.wearable.notification.model.db.User;
import com.oracle.cloud.wearable.notification.model.db.UserAlertNotifications;
import com.oracle.cloud.wearable.notification.service.UserAlertNotificationsService;
import com.oracle.cloud.wearable.notification.service.UserService;
import com.oracle.cloud.wearable.notification.util.EmailSender;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@Scope("prototype")
public class Worker implements Runnable {

    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final EventFormat FORMAT =
            EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE);

    private static final String KEY_EVENT_JSON = "jsonstring";

    private static final Integer DEFAULT_FREQUENCY = 15;

    private final String rawMessage;

    @Autowired
    private UserAlertNotificationsService alertNotificationsService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailSender emailSender;

    public Worker(final String msg) {
        rawMessage = msg;
    }

    public void run() {
        if (rawMessage == null || rawMessage.trim().isEmpty()) {
            log.warn("Incorrect alert event received, its empty, exiting");
            return;
        }

        log.debug("Raw event received on queue {}", rawMessage);

        try {
            final Map<String, String> tempHolder = MAPPER.readValue(rawMessage, Map.class);

            final String base64EncodedEvent = tempHolder.get(KEY_EVENT_JSON);
            if (base64EncodedEvent == null || base64EncodedEvent.trim().isEmpty()) {
                log.warn("Base64 encoded event is empty, exiting");
                return;
            }

            final byte[] decodedEvent = Base64.decodeBase64(base64EncodedEvent);
            final CloudEvent event = FORMAT.deserialize(decodedEvent);

            if (event != null) {
                final byte[] payload = event.getData().toBytes();
                if (payload == null || payload.length == 0) {
                    log.warn("Incorrect alert event received, has no data payload, exiting");
                    return;
                } else {
                    final String data = new String(payload);
                    log.info("Data in alert event is {}", data);
                    processEvent(data);
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Exception occurred while converting Json String to Alert object", e);
            throw new RuntimeException(
                    "Exception occurred while converting Json String to Alert object", e);
        } catch (Exception e) {
            log.error("Exception occurred while processing alert event", e);
            throw new RuntimeException("Exception occurred while processing alert event", e);
        }
    }

    private void processEvent(final String payload) throws JsonProcessingException {
        final Alert alert = MAPPER.readValue(payload, Alert.class);
        final UserAlertNotifications latestNotificationSentToUser =
                alertNotificationsService.getLatestNotificationSentToUser(
                        alert.getUsername(), alert.getDeviceSerialNumber());

        if (latestNotificationSentToUser == null
                || (latestNotificationSentToUser != null
                && isTimeIntervalOver(
                latestNotificationSentToUser.getNotificationTime(),
                alert.getNotificationFrequency()))) {

            final User user = userService.getUserByUsername(alert.getUsername());
            if (user == null) {
                throw new RuntimeException(
                        "User with email " + alert.getUsername() + " not found, throwing exception");
            }

            String recipient = user.getEmail();
            if (alert.getEmergencyContactEmail() != null) {
                recipient = alert.getEmergencyContactEmail();
            }
            emailSender.sendEmail(getMailBody(alert), recipient);

            if (latestNotificationSentToUser != null) {
                alertNotificationsService.saveUserAlertNotification(latestNotificationSentToUser, alert);
            } else {
                alertNotificationsService.saveUserAlertNotification(alert);
            }
        } else {
            log.info(
                    "Not sending alert for user {} with device {} as the time interval has not elapsed",
                    alert.getUsername(),
                    alert.getDeviceSerialNumber());
        }
    }

    private boolean isTimeIntervalOver(final Date notificationTime, Integer frequency) {
        if (notificationTime == null) {
            return true;
        }

        if (frequency == null) {
            frequency = DEFAULT_FREQUENCY;
        }

        return notificationTime.getTime() < System.currentTimeMillis() - (frequency * 60 * 1000);
    }

    private String getMailBody(final Alert alert) {
        final String body =
                "Dear %s, <br/><br/> An abnormal reading was observed for your device with serial number %s, for parameter %s, <b>Defined Threshold</b> is %s, <b>Observed Value</b> is %s";
        return String.format(
                body,
                alert.getUsername(),
                alert.getDeviceSerialNumber(),
                alert.getAlertingParameter(),
                alert.getThreshold(),
                alert.getObservedValue());
    }
}
