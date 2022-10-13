package com.oracle.cloud.wearable.admin.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.cloud.wearable.admin.UserPreferences;

public class UserPreferenceDaoImpl extends AbstractDao<UserPreferences> {


	private static ResultSetHandler<UserPreferences> userPrefResultSetHandler;
	private static ResultSetHandler<List<UserPreferences>> userPrefListResultSetHandler;
	
	private static final Logger log = Logger.getLogger(UserPreferenceDaoImpl.class.getName());

	private static ObjectMapper objectMapper = new ObjectMapper();

	static {

		userPrefResultSetHandler = new ResultSetHandler<UserPreferences>() {
			@Override
			public UserPreferences handle(ResultSet rs) throws SQLException {
				
				if(!rs.next())
					return null;
				
				ResultSetMetaData metaData = rs.getMetaData();
				
				UserPreferences obj = null;
				int columnCount = metaData.getColumnCount();
				
				if(columnCount==1) { // during insert
					int id = rs.getInt(1);
					obj = new UserPreferences();
					obj.setId(id);
				} else if(columnCount==3) {
					int id = rs.getInt("id");
					int userId = rs.getInt("user_id");
					String pref = rs.getString("preference");
					try {
						obj = objectMapper.readValue(pref, UserPreferences.class);
					} catch (Exception e) {
						throw new SQLException("unable to parse user preference object", e);
					}
					obj.setId(id);
					obj.setUserId(userId);
				}

				return obj;
			}
		};
		
		userPrefListResultSetHandler = new ResultSetHandler<List<UserPreferences>>() {
			@Override
			public List<UserPreferences> handle(ResultSet rs) throws SQLException {
				
				List<UserPreferences> preferences = new ArrayList<UserPreferences>();
				
				UserPreferences obj = null;
				do{
					obj = userPrefResultSetHandler.handle(rs);
					preferences.add(obj);
				}while (obj!=null);
				
				return preferences;
			}
		};

	}

	public UserPreferenceDaoImpl(Connection connection) {
		super(connection);
	}

	@Override
	public UserPreferences create(UserPreferences obj) throws DaoException {
		
		UserPreferences read = read(obj);
		if(read!=null) {
			throw new DaoException("Only one user pref row permitted per user");
		}
		
		final String sql = "INSERT into user_preference (user_id, preference) values (?,?)";
		QueryRunner run = new QueryRunner();

		String prefJson;
		try {
			prefJson = objectMapper.writeValueAsString(obj);
		} catch (Exception e1) {
			throw new DaoException("unable to convert user pref obj to string", e1);
		}

		try {
			return run.insert(getConnection(), sql, userPrefResultSetHandler, obj.getUserId(), prefJson);
		} catch (SQLException e) {
			throw new DaoException("unable to insert device", e);
		}
	}

	@Override
	public void update(UserPreferences obj) throws DaoException {
		
		String prefJson;
		try {
			prefJson = objectMapper.writeValueAsString(obj);
		} catch (Exception e1) {
			throw new DaoException("unable to convert user pref obj to string", e1);
		}

		String sql = "UPDATE user_preference SET preference=? ";

		QueryRunner run = new QueryRunner();

		try {
			run.update(getConnection(), sql, prefJson);
		} catch (SQLException e) {
			throw new DaoException("unable to update user pref", e);
		}
	}

	@Override
	public UserPreferences Delete(UserPreferences obj) throws DaoException {
		throw new DaoException("Delete pref not implimented");
	}

	@Override
	public UserPreferences read(UserPreferences obj) throws DaoException {
		String sql = "Select * from user_preference where ";

		Object param = null;

		if (obj.getUserId() != null) {
			sql = sql + "user_id=?";
			param = obj.getUserId();
		} else if (obj.getId() != null) {
			sql = sql + "id=?";
			param = obj.getId();
		} else {
			throw new DaoException("id or user id required to read preferences");
		}

		QueryRunner run = new QueryRunner();

		try {
			return run.query(getConnection(), sql, userPrefResultSetHandler, param);
		} catch (SQLException e) {
			throw new DaoException("error finding device", e);
		}
	}

	@Override
	public Collection<UserPreferences> find(UserPreferences obj) throws DaoException {
		String sql = "Select * from user_preference where ";

		Object param = null;

		if (obj.getUserId() != null) {
			sql = sql + "user_id=?";
			param = obj.getUserId();
		} else if (obj.getId() != null) {
			sql = sql + "id=?";
			param = obj.getId();
		} else {
			throw new DaoException("id or user id required to find preferences");
		}

		QueryRunner run = new QueryRunner();

		try {
			return run.query(getConnection(), sql, userPrefListResultSetHandler, param);
		} catch (SQLException e) {
			throw new DaoException("unable to find user preferences", e);
		}
	}

}
