package com.oracle.cloud.wearable.tcp.server.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.cloud.wearable.tcp.cache.DeviceCache;
import com.oracle.cloud.wearable.tcp.model.RawRequest;
import com.oracle.cloud.wearable.tcp.model.cloudevent.StreamEvent;
import com.oracle.cloud.wearable.tcp.model.db.Device;
import com.oracle.cloud.wearable.tcp.server.model.BaseModel;
import com.oracle.cloud.wearable.tcp.server.model.HealthReadingModel;
import com.oracle.cloud.wearable.tcp.server.service.MessageHandlerService;
import com.oracle.cloud.wearable.tcp.stream.MessageBuffer;
import com.oracle.cloud.wearable.tcp.util.EventType;
import io.cloudevents.json.Json;
import io.cloudevents.v1.CloudEventBuilder;
import io.cloudevents.v1.CloudEventImpl;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.UUID;

/** */
@Service
@Scope("prototype")
@Slf4j
public class MessageHandlerServiceImpl<T extends BaseModel> implements MessageHandlerService<T> {

  private static final String DELIMITER = ";";
  private static final String BP_DELIMITER = ":";
  private static final String DATA_CONTENT_TYPE = "application/json";
  private static final String SOURCE = "/oracle/cloud/health/monitoring/server";

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private final byte[] message;

  @Autowired private DeviceCache deviceCache;
  @Autowired private MessageBuffer messageBuffer;

  public MessageHandlerServiceImpl(final byte[] message) {
    this.message = message;
  }

  @Override
  public void run() {
    RawRequest data;

    try {
      if (message == null || message.length == 0) {
        log.warn("Empty message received, ignoring");
        return;
      }

      data = (RawRequest) SerializationUtils.deserialize(message);
      log.debug("Received message: {}", data);
    } catch (Exception e) {
      log.error(
          "Exception occurred while deserializing message {}, ignoring the message",
          new String(message),
          e);
      return;
    }

    final T obj = parsePayload(data.getPayload());
    if (obj == null) {
      return;
    }

    if (obj.getEventType() == EventType.READING) {
      log.info("Processing message, all pre checks passed");

      final HealthReadingModel readingModel = (HealthReadingModel) obj;
      final Device deviceInfo = deviceCache.getDeviceInfo(readingModel.getDeviceSerialNo());

      if (deviceInfo != null) {
        try {
          final CloudEventImpl<String> ce = getCloudEventObject(readingModel, deviceInfo);

          // push message to buffer
          final String encodedEvent = Json.encode(ce);
          messageBuffer.addMessageToBuffer(encodedEvent);
          log.debug("Pushed Cloud Event {} to buffer", encodedEvent);
        } catch (JsonProcessingException e) {
          log.error("Error!! couldn't write JSON when creating data element of cloud event ", e);
          throw new RuntimeException(e);
        }
      } else {
        log.warn(
            "Device with serial number {} not found in cache, ignoring the event",
            readingModel.getDeviceSerialNo());
      }
    }
  }

  private T parsePayload(final String payload) {
    final String[] tokens = payload.split(DELIMITER);

    if (tokens.length < 3) {
      log.warn("Incorrect payload received {}, returning null and not processing it", payload);
      return null;
    }

    final String deviceSerialNumber = tokens[0];
    final String eventType = tokens[1];

    if (eventType != null && !eventType.trim().isEmpty()) {
      if (eventType.equalsIgnoreCase(EventType.READING.getValue())) {
        final HealthReadingModel reading = getHealthReadingModel(tokens, payload);
        return (T) reading;
      } else {
        // TODO: implement other event parsers
        BaseModel baseModel = new BaseModel();
        populateBaseParams(
            (T) baseModel, deviceSerialNumber, payload, EventType.SOS, tokens[tokens.length - 1]);
        return (T) baseModel;
      }
    }
    return null;
  }

  private HealthReadingModel getHealthReadingModel(final String[] tokens, final String payload) {
    final Integer heartRate = Integer.parseInt(tokens[2]);
    final String[] bpReadings = tokens[3].split(BP_DELIMITER);
    final Integer systolicBP = Integer.parseInt(bpReadings[0]);
    final Integer diastolicBP = Integer.parseInt(bpReadings[1]);
    final Double spo2Level = Double.parseDouble(tokens[4]);

    final HealthReadingModel healthReadingModel =
        new HealthReadingModel(heartRate, systolicBP, diastolicBP, spo2Level);
    populateBaseParams(
        (T) healthReadingModel, tokens[0], payload, EventType.READING, tokens[tokens.length - 1]);

    return healthReadingModel;
  }

  private void populateBaseParams(
      @NotNull final T model,
      final String deviceSerialNo,
      final String payload,
      final EventType eventType̵̵,
      final String readingTime) {
    model.setDeviceSerialNo(deviceSerialNo);
    model.setPayload(payload);
    model.setEventType(eventType̵̵);
    model.setReadingTime(Long.parseLong(readingTime));
    model.setTotalLength(payload.length());
  }

  private CloudEventImpl<String> getCloudEventObject(
      final HealthReadingModel readingModel, final Device device) throws JsonProcessingException {
    final StreamEvent streamEvent = new StreamEvent();
    streamEvent.setUsername(device.getUser().getUsername());
    streamEvent.setUserId(device.getUser().getId());
    streamEvent.setDeviceSerialNumber(readingModel.getDeviceSerialNo());
    streamEvent.setHeartRate(readingModel.getHeartRate());
    streamEvent.setReadingTime(readingModel.getReadingTime());
    streamEvent.setSystolicBP(readingModel.getSystolicBP());
    streamEvent.setDiastolicBP(readingModel.getDiastolicBP());
    streamEvent.setSpo2Level(readingModel.getSpo2Level());
    streamEvent.setEventType(readingModel.getEventType());

    return CloudEventBuilder.<String>builder()
        .withData(MAPPER.writeValueAsString(streamEvent))
        .withDataContentType(DATA_CONTENT_TYPE)
        .withId(UUID.randomUUID().toString())
        .withSource(URI.create(SOURCE))
        .withType("StreamEvent")
        .withTime(ZonedDateTime.now())
        .build();
  }
}
