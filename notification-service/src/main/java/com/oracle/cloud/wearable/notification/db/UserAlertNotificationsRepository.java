package com.oracle.cloud.wearable.notification.db;

import com.oracle.cloud.wearable.notification.model.db.UserAlertNotifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAlertNotificationsRepository
    extends JpaRepository<UserAlertNotifications, Long> {

  @Query(
      nativeQuery = true,
      value =
          "SELECT uan.* FROM user_alert_notifications uan, device d, user u WHERE u.username=:username and d.serial_number=:deviceSerialNumber and u.id = d.user_id and uan.user_id = u.id and uan.device_id = d.id and uan.alert_parameter=:alertParameter ORDER BY notification_time DESC LIMIT 1")
  Optional<UserAlertNotifications> getLatestNotificationSentToUser(
      final String username, final String deviceSerialNumber, final String alertParameter);
}