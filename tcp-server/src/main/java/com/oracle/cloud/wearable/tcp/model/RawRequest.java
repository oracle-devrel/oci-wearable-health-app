package com.oracle.cloud.wearable.tcp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/** */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawRequest implements Serializable {
  private String payload;
}
