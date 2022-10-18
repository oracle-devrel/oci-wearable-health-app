package com.oracle.cloud.wearable.notification.service;

import com.oracle.cloud.wearable.notification.model.Alert;
import com.oracle.cloud.wearable.notification.model.db.UserAlertNotifications;

public interface UserAlertNotificationsService<T> {

  UserAlertNotifications getLatestNotificationSentToUser(
      final String username, final String serialNumber);

  void saveUserAlertNotification(
      final UserAlertNotifications lastAlertNotification, final Alert alert);

  void saveUserAlertNotification(final Alert alert);
}