package com.oracle.cloud.wearable.admin.service;

public class ServiceException extends Exception {
	
	private ServiceError error;

	public ServiceException() {
		// TODO Auto-generated constructor stub
	}

	public ServiceException(ServiceError message) {
		super(message.getDetailMessage());
		this.error=message;
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

	public ServiceException(ServiceError message, Throwable cause) {
		super(message.getDetailMessage(), cause);
		this.error=message;
	}

	public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ServiceError getError() {
		return error;
	}
	
}
