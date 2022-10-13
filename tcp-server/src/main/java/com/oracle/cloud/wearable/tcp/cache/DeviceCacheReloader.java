package com.oracle.cloud.wearable.tcp.cache;

import com.oracle.cloud.wearable.tcp.db.DeviceRepository;
import com.oracle.cloud.wearable.tcp.model.db.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class DeviceCacheReloader {

  @Autowired
  private DeviceCache deviceCache;

  @Autowired
  private DeviceRepository deviceRepository;

  @Scheduled(fixedDelay = 600 * 1000L, initialDelay = 600 * 1000L)
  public void reload() {
    log.info("Device Cache Refresher triggered at time {}", new Date());
    final List<Device> devices = deviceRepository.findAll();
    deviceCache.loadCache(devices);
    log.info("Device cache refresh complete at time {}", new Date());
  }
}
