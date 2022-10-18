package com.oracle.cloud.wearable.tcp.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthReadingModel extends BaseModel {
  private Integer heartRate;
  private Integer systolicBP;
  private Integer diastolicBP;
  private Double spo2Level;
}