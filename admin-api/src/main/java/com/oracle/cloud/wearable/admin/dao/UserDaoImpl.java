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

import com.oracle.cloud.wearable.admin.User;
import com.oracle.cloud.wearable.admin.fn.Utils;

public class UserDaoImpl extends AbstractDao<User> {


	private static ResultSetHandler<User> userResultSetHandler;
	private static ResultSetHandler<List<User>> userListResultSetHandler;
	
	static {
		HashMap<String, String> columnToPropertyOverrides = new HashMap<String, String>();
		
		columnToPropertyOverrides.put("first_name", "userId");
		columnToPropertyOverrides.put("last_name", "activationDate");
		
		BeanProcessor beanProcessor = new BeanProcessor(columnToPropertyOverrides);
		BasicRowProcessor rowProcessor = new BasicRowProcessor(beanProcessor);
		
		userResultSetHandler = new BeanHandler<User>(User.class,rowProcessor);
		userListResultSetHandler = new BeanListHandler<User>(User.class, rowProcessor);
	}

	public UserDaoImpl(Connection connection) {
		super(connection);
	}

	@Override
	public User create(User obj) throws DaoException {
		final String sql = "INSERT into user (username, password, first_name, last_name, mobile, email, status) values (?,?,?,?,?,?,?)";
		QueryRunner run = new QueryRunner();
		
		try {
			return run.insert(getConnection(),sql,
					userResultSetHandler, obj.getUsername(), obj.getPassword(),obj.getFirstName(),obj.getLastName(),obj.getMobile(),obj.getEmail(),obj.getStatus());
		} catch (SQLException e) {
			throw new DaoException("unable to insert device",e);
		}
	}

	@Override
	public void update(User obj) throws DaoException {
		StringBuilder sb = new StringBuilder();
		
		List<Object> objParams = new ArrayList<Object>();
		
		sb.append("UPDATE user SET ");
		if(!Utils.isEmptyString(obj.getStatus())) {
			sb.append("status=? ");
			objParams.add(obj.getStatus());
		}
		
		if(objParams.size()==0)
			return;
		
		String sql = sb.toString();
		QueryRunner run = new QueryRunner();

		try {
			 run.update(getConnection(), sql, userResultSetHandler, objParams.toArray());
		} catch (SQLException e) {
			throw new DaoException("unable to update device", e);
		}
	}

	@Override
	public User Delete(User obj) throws DaoException {
		throw new DaoException("Delete user not implimented");
	}

	@Override
	public User read(User obj) throws DaoException {
		final String sql = "Select * from user where username=?";
		QueryRunner run = new QueryRunner();
		
		try {
			return run.query(getConnection(), sql, userResultSetHandler,obj.getUsername());
		} catch (SQLException e) {
			throw new DaoException("unable to find user by username"+obj.getUsername(), e);
		}
	}

	@Override
	public Collection<User> find(User obj) throws DaoException {
		String sql = "Select * from user where ";
		
		Object param = null;
		
		if(obj.getUsername()!=null) {
			sql = sql + "username=?";
			param=obj.getUsername();
		}else if(obj.getMobile()!=null) {
			sql = sql + "mobile=?";
			param=obj.getMobile();
		}else if(obj.getEmail()!=null) {
			sql = sql + "email=?";
			param=obj.getEmail();
		}else {
			throw new DaoException("any of username / mobile / email / type required to find user");
		}
		
		QueryRunner run = new QueryRunner();

		try {
			return run.query(getConnection(), sql, userListResultSetHandler, param);
		} catch (SQLException e) {
			throw new DaoException("unable to find user", e);
		}
	}

}
