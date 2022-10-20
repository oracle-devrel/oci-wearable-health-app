package com.oracle.cloud.wearable.notification.service.impl;

import com.oracle.cloud.wearable.notification.db.DeviceRepository;
import com.oracle.cloud.wearable.notification.db.UserAlertNotificationsRepository;
import com.oracle.cloud.wearable.notification.db.UserRepository;
import com.oracle.cloud.wearable.notification.model.Alert;
import com.oracle.cloud.wearable.notification.model.db.Device;
import com.oracle.cloud.wearable.notification.model.db.User;
import com.oracle.cloud.wearable.notification.model.db.UserAlertNotifications;
import com.oracle.cloud.wearable.notification.service.UserAlertNotificationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class UserAlertNotificationsServiceImpl
    implements UserAlertNotificationsService<UserAlertNotifications> {

  @Autowired private UserAlertNotificationsRepository alertNotificationsRepository;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private UserRepository userRepository;

  public UserAlertNotifications getLatestNotificationSentToUser(
      final String username, final String serialNumber, final String alertParameter) {
    final Optional<UserAlertNotifications> latestNotificationSentToUser =
        alertNotificationsRepository.getLatestNotificationSentToUser(
            username, serialNumber, alertParameter);
    if (latestNotificationSentToUser.isPresent()) {
      return latestNotificationSentToUser.get();
    }
    return null;
  }

  public void saveUserAlertNotification(
      final UserAlertNotifications lastAlertNotification, final Alert alert) {
    alertNotificationsRepository.save(createAlertNotifications(lastAlertNotification, alert));
  }

  public void saveUserAlertNotification(final Alert alert) {
    final Optional<Device> device =
        deviceRepository.getDeviceBySerialNumber(alert.getDeviceSerialNumber());
    if (!device.isPresent()) {
      log.error("Device with serial number {} not found ", alert.getDeviceSerialNumber());
      throw new RuntimeException(
          "Device with serial number " + alert.getDeviceSerialNumber() + " not found ");
    }

    final Optional<User> user = userRepository.getUserByUsername(alert.getUsername());
    if (!user.isPresent()) {
      log.error("User with username {} not found ", alert.getUsername());
      throw new RuntimeException("User with username " + alert.getUsername() + " not found ");
    }

    final UserAlertNotifications lastAlertNotification = new UserAlertNotifications();
    lastAlertNotification.setDevice(device.get());
    lastAlertNotification.setUser(user.get());
    alertNotificationsRepository.save(createAlertNotifications(lastAlertNotification, alert));
  }

  private UserAlertNotifications createAlertNotifications(
      final UserAlertNotifications lastAlertNotification, final Alert alert) {
    final UserAlertNotifications newAlertNotifications = new UserAlertNotifications();

    final Device d = new Device();
    d.setId(lastAlertNotification.getDevice().getId());
    newAlertNotifications.setDevice(d);

    final User u = new User();
    u.setId((lastAlertNotification.getUser().getId()));
    newAlertNotifications.setUser(u);

    newAlertNotifications.setAlertId(alert.getAlertID());
    newAlertNotifications.setAlertParameter(alert.getAlertingParameter());
    newAlertNotifications.setNotificationChannel(alert.getNotificationChannel());
    newAlertNotifications.setNotificationTime(new Date());
    newAlertNotifications.setObservedValue(alert.getObservedValue());

    return newAlertNotifications;
  }
}