package com.oracle.cloud.wearable.admin.dao;

import java.util.Collection;

public interface Dao<T> {

	public T create(T obj) throws DaoException;

	public void update(T obj) throws DaoException;

	public T Delete(T obj) throws DaoException;

	public T read(T obj) throws DaoException;

	public Collection<T> find(T obj) throws DaoException;

}
