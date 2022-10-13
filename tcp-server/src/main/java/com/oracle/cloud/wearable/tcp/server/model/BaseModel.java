package com.oracle.cloud.wearable.tcp.server.model;

import com.oracle.cloud.wearable.tcp.util.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseModel {
  private Integer totalLength;
  private String payload;
  private String deviceSerialNo;
  private EventType eventType;
  private Long readingTime;
}
