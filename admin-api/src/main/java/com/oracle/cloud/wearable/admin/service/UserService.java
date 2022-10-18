package com.oracle.cloud.wearable.admin.service;

import com.oracle.cloud.wearable.admin.User;
import com.oracle.cloud.wearable.admin.UserPreferences;

public interface UserService {
	
	public void addUser(User user) throws ServiceException;
	
	public void addUserPreferences(String username, UserPreferences userPreferences) throws ServiceException;
	
	public void updateUserPreferences(String username, UserPreferences userPreferences) throws ServiceException;
	
	public User findUserByUserName(String username) throws ServiceException;
	
	public UserPreferences getUserPreferences(String username) throws ServiceException;

	void verfiyCredentials(byte[] pass, String username) throws ServiceException;

}
