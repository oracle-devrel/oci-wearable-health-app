package com.oracle.cloud.wearable.admin.service;

import javax.sql.DataSource;

import com.oracle.cloud.wearable.admin.Device;
import com.oracle.cloud.wearable.admin.User;
import com.oracle.cloud.wearable.admin.UserPreferences;
import com.oracle.cloud.wearable.admin.dao.DaoException;
import com.oracle.cloud.wearable.admin.dao.DaoFactory;

public class ServiceFactory {

	private static ServiceFactory instance = new ServiceFactory();

	private ServiceFactory() {
	}

	public static ServiceFactory getInstance() {
		return instance;
	}

	public <T extends Object> T getService(Class<T> class1, DataSource dataSource) throws ServiceException {

		try {

			if (class1.isAssignableFrom(DeviceService.class)) {
				return (T) new DeviceServiceImpl(DaoFactory.getInstance().getDao(Device.class, dataSource),DaoFactory.getInstance().getDao(User.class, dataSource));
			} else if (class1.isAssignableFrom(UserService.class)) {
				return (T) new UserServiceImpl(DaoFactory.getInstance().getDao(User.class, dataSource),DaoFactory.getInstance().getDao(UserPreferences.class, dataSource));
			} else {
				throw new RuntimeException("No service instance found for" + class1);
			}
			
		} catch (DaoException e) {
			throw new ServiceException(ServiceError._300,e);
		}

	}

}
