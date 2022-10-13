package com.oracle.cloud.wearable.admin.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.oracle.cloud.wearable.admin.Device;
import com.oracle.cloud.wearable.admin.User;
import com.oracle.cloud.wearable.admin.UserPreferences;

public class DaoFactory {

	private static final Logger log = Logger.getLogger(DaoFactory.class.getName());

	private static DaoFactory instance = new DaoFactory();
	private Connection con;

	public static DaoFactory getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> Dao<T> getDao(Class<T> clazz, DataSource dataSource) throws DaoException {

		MysqlDataSource ds = (MysqlDataSource) dataSource;
		try {

			if (con == null || con.isClosed()) {
				log.log(Level.FINE, "DS user-" + ds.getUser());
				log.log(Level.FINE, "DS url-" + ds.getUrl());
				log.log(Level.FINE, "DS host-" + ds.getServerName());

				log.log(Level.FINE, "Creating DB connection");

				final Connection connection = dataSource.getConnection();

				con = new ConnectionWrapper(connection);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (clazz.isAssignableFrom(Device.class)) {
			return (Dao<T>) new DeviceDaoImpl(con);
		} else if (clazz.isAssignableFrom(User.class)) {
			return (Dao<T>) new UserDaoImpl(con);
		} else if (clazz.isAssignableFrom(UserPreferences.class)) {
			return (Dao<T>) new UserPreferenceDaoImpl(con);
		} else if (clazz.isAssignableFrom(UserPreferences.class)) {
			throw new DaoException("user pref dao impl not available");
		} else {
			throw new DaoException("Dao factory unable to create instace for class" + clazz.getCanonicalName());
		}

	}

}
