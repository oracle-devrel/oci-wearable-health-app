package com.oracle.cloud.wearable.admin.dao;

import java.sql.Connection;

public abstract class AbstractDao<T> implements Dao<T> {

	private Connection con;

	public AbstractDao(Connection connection) {
		this.con = connection;
	}
	
	protected Connection getConnection() {
		return con;
	}

}
