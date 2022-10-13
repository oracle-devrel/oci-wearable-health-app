package com.oracle.cloud.wearable.admin;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceList {

	@JsonProperty
	private Collection<Device> deviceList;

	public DeviceList() {
	}

	public DeviceList(Collection<Device> deviceList) {
		super();
		this.deviceList = deviceList;
	}

	public Collection<Device> getDeviceList() {
		return deviceList;
	}

	public void setDeviceList(Collection<Device> deviceList) {
		this.deviceList = deviceList;
	}

}
