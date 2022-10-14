package com.oracle.cloud.wearable.admin.service;

import org.apache.commons.codec.digest.MurmurHash2;

import com.oracle.cloud.wearable.admin.User;
import com.oracle.cloud.wearable.admin.UserPreferences;
import com.oracle.cloud.wearable.admin.dao.Dao;
import com.oracle.cloud.wearable.admin.dao.DaoException;
import com.oracle.cloud.wearable.admin.fn.Utils;

public class UserServiceImpl implements UserService {

	private static final int SEED = 3848758;

	private Dao<User> userDao;
	private Dao<UserPreferences> userPrefDao;

	public UserServiceImpl(Dao<User> dao, Dao<UserPreferences> userPrefDao) {
		this.userDao = dao;
		this.userPrefDao = userPrefDao;
	}
	
	@Override
	public void verfiyCredentials(byte[] pass, String username) throws ServiceException{
		
		User user = findUserByUserName(username);
		if(user==null)
			throw new ServiceException(ServiceError._302);
		
		long hash = MurmurHash2.hash64(pass, pass.length, SEED);
		
		if(!String.valueOf(hash).equals(user.getPassword())) {
			throw new ServiceException(ServiceError._400);
		}
		
	}

	@Override
	public void addUser(User user) throws ServiceException {

		final String password = user.getPassword();

		if (Utils.isEmptyString(user.getEmail()) || Utils.isEmptyString(password) || Utils.isEmptyString(user.getStatus())  ) {
			throw new ServiceException(ServiceError._401);
		} 

		validatePassword(password);
		final byte[] bytes = password.getBytes();

		long hash = MurmurHash2.hash64(bytes, bytes.length, SEED);

		user.setPassword(String.valueOf(hash));

		user.setUsername(user.getEmail());

		try {
			userDao.create(user);
		} catch (DaoException e) {
			throw new ServiceException(ServiceError._402, e);
		}

	}
	
	@Override
	public void addUserPreferences(String username, UserPreferences userPreferences) throws ServiceException {
		User user = findUserByUserName(username);
		if(user==null)
			throw new ServiceException(ServiceError._302);
		
		userPreferences.setUserId(user.getId());
		
		try {
			userPrefDao.create(userPreferences);
		} catch (DaoException e) {
			throw new ServiceException(ServiceError._502, e);
		}
		
		return;
	}
	
	@Override
	public void updateUserPreferences(String username, UserPreferences newUserPreferences) throws ServiceException {
	    
	    UserPreferences oldUserPreferences = getUserPreferences(username);
	    
	    if(oldUserPreferences==null)
	        throw new ServiceException(ServiceError._503);
	    
	    newUserPreferences.setId(oldUserPreferences.getId());

	    if(Utils.isEmptyString(newUserPreferences.getAlertThresholdForBPHigh()))
	        newUserPreferences.setAlertThresholdForBPHigh(oldUserPreferences.getAlertThresholdForBPHigh());
	    
	    if(Utils.isEmptyString(newUserPreferences.getAlertThresholdForBPLow()))
            newUserPreferences.setAlertThresholdForBPLow(oldUserPreferences.getAlertThresholdForBPLow());
	    
	    if(Utils.isEmptyString(newUserPreferences.getAlertThresholdForHearRate()))
            newUserPreferences.setAlertThresholdForHearRate(oldUserPreferences.getAlertThresholdForHearRate());
	    
	    if(Utils.isEmptyString(newUserPreferences.getAlertThresholdForSPO2()))
            newUserPreferences.setAlertThresholdForSPO2(oldUserPreferences.getAlertThresholdForSPO2());
	    
	    if(Utils.isEmptyString(newUserPreferences.getAlertThresholdForTemp()))
            newUserPreferences.setAlertThresholdForTemp(oldUserPreferences.getAlertThresholdForTemp());
	    
	    if(Utils.isEmptyString(newUserPreferences.getEmergencyEmail()))
            newUserPreferences.setEmergencyEmail(oldUserPreferences.getEmergencyEmail());
	    
	    if(Utils.isEmptyString(newUserPreferences.getEmergencyMobile()))
            newUserPreferences.setEmergencyMobile(oldUserPreferences.getEmergencyMobile());
	    
	    if(Utils.isEmptyString(newUserPreferences.getPreferedAlertChannel()))
            newUserPreferences.setPreferedAlertChannel(oldUserPreferences.getPreferedAlertChannel());
	    
	    
	    try {
            userPrefDao.update(newUserPreferences);
        } catch (DaoException e) {
            throw new ServiceException(ServiceError._504, e);
        }
	    
	    
	}

	private void validatePassword(String password) throws ServiceException {
		if (password.length() < 8)
			throw new ServiceException(ServiceError._403);

	}

	@Override
	public User findUserByUserName(String username) throws ServiceException {
		if (Utils.isEmptyString(username))
			return null;

		final User obj = new User();
		obj.setUsername(username);
		try {
			return userDao.read(obj);
		} catch (DaoException e) {
			throw new ServiceException(ServiceError._301, e);
		}
	}
	
	@Override
	public UserPreferences getUserPreferences(String username) throws ServiceException {
		User findUserByUserName = findUserByUserName(username);
		
		if(findUserByUserName==null)
			throw new ServiceException(ServiceError._302);
		
		UserPreferences userPref = new UserPreferences();
		
		userPref.setUserId(findUserByUserName.getId());
		
		try {
			return userPrefDao.read(userPref);
		} catch (DaoException e) {
			throw new ServiceException(ServiceError._503, e);
		}
		
	}

}
