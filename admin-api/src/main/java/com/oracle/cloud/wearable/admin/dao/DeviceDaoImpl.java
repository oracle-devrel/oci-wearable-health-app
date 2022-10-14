package com.oracle.cloud.wearable.admin.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.oracle.cloud.wearable.admin.Device;
import com.oracle.cloud.wearable.admin.fn.Utils;

public class DeviceDaoImpl extends AbstractDao<Device> {

	private static ResultSetHandler<Device> deviceResultSetHandler;
	private static ResultSetHandler<List<Device>> deviceListResultSetHandler;

	static {
		HashMap<String, String> columnToPropertyOverrides = new HashMap<String, String>();

		columnToPropertyOverrides.put("id", "id");
		columnToPropertyOverrides.put("serial_number", "deviceSerialNumber");
		columnToPropertyOverrides.put("status", "status");
		columnToPropertyOverrides.put("user_id", "userId");
		columnToPropertyOverrides.put("activation_date", "activationDate");
		columnToPropertyOverrides.put("device_type", "deviceType");

		BeanProcessor beanProcessor = new BeanProcessor(columnToPropertyOverrides);
		BasicRowProcessor rowProcessor = new BasicRowProcessor(beanProcessor);

		deviceResultSetHandler = new BeanHandler<Device>(Device.class, rowProcessor);
		deviceListResultSetHandler = new BeanListHandler<Device>(Device.class, rowProcessor);
	}

	public DeviceDaoImpl(Connection connection) {
		super(connection);
	}

	@Override
	public Device create(Device obj) throws DaoException {
		final String sql = "INSERT into device (serial_number, status, user_id, device_type, activation_date) values (?,?,?,?,?)";
		QueryRunner run = new QueryRunner();

		try {
			return run.insert(getConnection(), sql, deviceResultSetHandler, obj.getDeviceSerialNumber(),
					obj.getStatus(), obj.getUserId(), obj.getDeviceType(), obj.getActivationDate());
		} catch (SQLException e) {
			throw new DaoException("unable to insert device", e);
		}

	}

	@Override
	public void update(Device obj) throws DaoException {
		StringBuilder sb = new StringBuilder();
		
		List<Object> objParams = new ArrayList<Object>();
		
		sb.append("UPDATE device SET ");
		if(!Utils.isEmptyString(obj.getStatus())) {
			sb.append("status=? ");
			objParams.add(obj.getStatus());
		}
		if(obj.getUserId()!=null) {
			sb.append("user_id=? ");
			objParams.add(obj.getUserId());
		}
		
		if(Utils.isEmptyString(obj.getStatus())) {
            sb.append("status=? ");
            objParams.add(obj.getStatus());
        }
		
		if(objParams.size()==0)
			return;
		
		sb.append(" where serial_number=?"); // update based on serial number of device
		objParams.add(obj.getDeviceSerialNumber());
		
		String sql = sb.toString();
		QueryRunner run = new QueryRunner();

		try {
			 run.update(getConnection(), sql, objParams.toArray());
		} catch (SQLException e) {
			throw new DaoException("unable to update device", e);
		}
	}

	@Override
	public Device Delete(Device obj) throws DaoException {
		throw new DaoException("Delete device not implimented");
	}

	@Override
	public Device read(Device obj) throws DaoException {
		String sql = "Select * from device where ";
		
		Object param = null;
		
		if(obj.getId()!=null) {
			sql = sql + "user_id=?";
			param=obj.getId();
		}else if(obj.getDeviceSerialNumber()!=null) {
			sql = sql + "serial_number=?";
			param=obj.getDeviceSerialNumber();
		}else {
			throw new DaoException("id or device serial number required to read device");
		}
		
		QueryRunner run = new QueryRunner();

		try {
			return run.query(getConnection(), sql, deviceResultSetHandler, param);
		} catch (SQLException e) {
			throw new DaoException("error finding device", e);
		}
	}

	@Override
	public Collection<Device> find(Device obj) throws DaoException {
		String sql = "Select * from device where ";
		
		Object param = null;
		
		if(obj.getUserId()!=null) {
			sql = sql + "user_id=?";
			param=obj.getUserId();
		}else if(obj.getDeviceSerialNumber()!=null) {
			sql = sql + "serial_number=?";
			param=obj.getDeviceSerialNumber();
		}else if(obj.getStatus()!=null) {
			sql = sql + "status=?";
			param=obj.getStatus();
		}else if(obj.getDeviceType()!=null) {
			sql = sql + "device_type=?";
			param=obj.getDeviceType();
		}else {
			throw new DaoException("any of id /serial no / status / type required to list device");
		}
		
		QueryRunner run = new QueryRunner();

		try {
			return run.query(getConnection(), sql, deviceListResultSetHandler, param);
		} catch (SQLException e) {
			throw new DaoException("unable to find device", e);
		}
	}

}
