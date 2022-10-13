package com.oracle.cloud.wearable.tcp.util;

/** */
public enum EventType {
  SOS("SOS"),
  FALL("FALL"),
  READING("READING");

  private String val;

  private EventType(final String val) {
    this.val = val;
  }

  public String getValue() {
    return this.val;
  }
}
