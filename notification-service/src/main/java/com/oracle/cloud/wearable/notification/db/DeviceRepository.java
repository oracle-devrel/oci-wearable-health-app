package com.oracle.cloud.wearable.notification.db;

import com.oracle.cloud.wearable.notification.model.db.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query(value = "SELECT d FROM Device d WHERE d.serialNumber = :serialNumber")
    Optional<Device> getDeviceBySerialNumber(final String serialNumber);
}