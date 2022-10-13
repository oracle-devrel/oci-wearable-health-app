package com.oracle.cloud.wearable.tcp.db;

import com.oracle.cloud.wearable.tcp.model.db.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    //
}