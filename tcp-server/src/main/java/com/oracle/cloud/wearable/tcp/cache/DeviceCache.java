package com.oracle.cloud.wearable.tcp.cache;

import com.oracle.cloud.wearable.tcp.db.DeviceRepository;
import com.oracle.cloud.wearable.tcp.model.db.Device;
import com.oracle.cloud.wearable.tcp.util.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Scope("singleton")
public final class DeviceCache {
    private final Map<String, Device> cache;

    @Autowired
    DeviceCache(final DeviceRepository deviceRepository) {
        cache = new ConcurrentHashMap<>();
        final List<Device> devices = deviceRepository.findAll();
        log.info("Device Info fetched from DB, total devices found {}", devices.size());
        loadCache(devices);
    }

    synchronized void loadCache(final List<Device> deviceList) {
        deviceList.forEach(device -> {
            if(device.getStatus().equalsIgnoreCase(Status.ACTIVE.toString())) {
                cache.put(device.getSerialNumber(), device);
            } else {
                log.info("Device with serial number {} is inactive, not adding it into cache", device.getSerialNumber());
            }
        });
    }

    public synchronized Device getDeviceInfo(final String serialNumber) {
        return cache.get(serialNumber);
    }
}