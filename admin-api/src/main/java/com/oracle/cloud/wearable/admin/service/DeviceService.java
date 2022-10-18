package com.oracle.cloud.wearable.admin.service;

import java.util.Collection;

import com.oracle.cloud.wearable.admin.Device;
import com.oracle.cloud.wearable.admin.User;

public interface DeviceService {
	
	public void createDevice(String username, Device d) throws ServiceException;
	
	public void linkDevice(User u,Device d) throws ServiceException;
	
	public void getDevice(String serialNo) throws ServiceException;
	
	public Collection<Device> getDevices(String userId) throws ServiceException;

}
