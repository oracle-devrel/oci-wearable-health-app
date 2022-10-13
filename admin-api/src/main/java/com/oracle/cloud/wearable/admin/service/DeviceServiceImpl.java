package com.oracle.cloud.wearable.admin.service;

import java.util.Collection;

import com.oracle.cloud.wearable.admin.Device;
import com.oracle.cloud.wearable.admin.User;
import com.oracle.cloud.wearable.admin.dao.Dao;
import com.oracle.cloud.wearable.admin.dao.DaoException;
import com.oracle.cloud.wearable.admin.fn.Utils;

public class DeviceServiceImpl implements DeviceService {

	private Dao<User> userDao;
	private Dao<Device> deviceDao;

	public DeviceServiceImpl(Dao<Device> dao, Dao<User> userDao) {
		this.userDao = userDao;
		this.deviceDao = dao;
	}

	@Override
	public void createDevice(String username, Device d) throws ServiceException {
		
		Collection<User> find;
		User u = new User();
		u.setUsername(username);
		try {
			find = userDao.find(u);
		} catch (DaoException e1) {
			throw new ServiceException(ServiceError._301,e1);
		}
		
		if(find==null || find.isEmpty())
			throw new ServiceException(ServiceError._302);
		else if(find.size()>1)
			throw new ServiceException(ServiceError._303);
		else
			u = (User) find.toArray()[0];
		
		d.setUserId(u.getId());
		
		if(Utils.isEmptyString(d.getStatus())||Utils.isEmptyString(d.getDeviceSerialNumber()))
			throw new ServiceException(ServiceError._304);

		try {
			deviceDao.create(d);
		} catch (DaoException e) {
			throw new ServiceException(ServiceError._305,e);
		}

	}

	@Override
	public void linkDevice(User u, Device d) throws ServiceException {
		
		if(Utils.isEmptyString(d.getDeviceSerialNumber()))
			throw new ServiceException(ServiceError._307);
		
		Device device = new Device();
		device.setDeviceSerialNumber(d.getDeviceSerialNumber());
		
		Collection<Device> deviceList = null;
		try {
			deviceList = deviceDao.find(device);
		} catch (DaoException e) {
			throw new ServiceException(ServiceError._306, e);
		}
		
		if(deviceList!=null && deviceList.size()!=1)
			throw new ServiceException(ServiceError._308);
		
		if(((Device)deviceList.toArray()[0]).getUserId()!=null)
			throw new ServiceException(ServiceError._310);
		
		try {
			User read = userDao.read(u);

			if (read == null)
				throw new ServiceException(ServiceError._301);
			
			d.setUserId(read.getId());
			
			deviceDao.update(d);
			
		} catch (DaoException e) {
			throw new ServiceException(ServiceError._309, e);
		}
		

	}

	@Override
	public void getDevice(String serialNo) {

	}

	@Override
	public Collection<Device> getDevices(String userName) throws ServiceException {

		final User obj = new User();
		obj.setUsername(userName);

		try {
			User read = userDao.read(obj);
			
			if(read==null)
				throw new ServiceException(ServiceError._301);
			
			Device device = new Device();
			device.setUserId(read.getId());

			return deviceDao.find(device);
		} catch (DaoException e) {
			throw new ServiceException(ServiceError._306, e);
		}
	}

}
