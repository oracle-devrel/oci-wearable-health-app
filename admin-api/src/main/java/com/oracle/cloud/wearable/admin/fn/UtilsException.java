package com.oracle.cloud.wearable.admin.fn;

import com.oracle.cloud.wearable.admin.response.Error;

public class UtilsException extends RuntimeException {
	
	private Error errorResponse;

	public UtilsException() {
	}

	public UtilsException(String message) {
		super(message);
	}

	public UtilsException(Throwable cause) {
		super(cause);
	}

	public UtilsException(String message, Throwable cause) {
		super(message, cause);
	}

	public UtilsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UtilsException(String string, Exception e, Error error) {
		super(string, e);
		this.errorResponse=error;
	}
	
	public Error getErrorResponse() {
		return errorResponse;
	}

}
