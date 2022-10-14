package com.oracle.cloud.wearable.admin.service;

public class ServiceError {

	private int code;
	private String userMessage;
	private String detailMessage;

	public ServiceError(int code, String userMsg, String detailMsg) {
		this.code = code;
		this.userMessage = userMsg;
		this.detailMessage = detailMsg;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getDetailMessage() {
		return detailMessage;
	}
	
	public String getUserMessage() {
		return userMessage;
	}

	public static ServiceError _800 = new ServiceError(800, "Service Error", "Unable to fetch secret by OCID");
	
	public static ServiceError _300 = new ServiceError(300, "Service Error", "Unable to create Service in factory");

	public static ServiceError _301 = new ServiceError(301, "Service Error", "Error finding user by username");
	public static ServiceError _302 = new ServiceError(302, "User not found", "User not found");
	public static ServiceError _303 = new ServiceError(303, "Too many users found", "Too many users found to add device");
	public static ServiceError _304 = new ServiceError(304, "status and serial number are mandatory for a device.", "status and  serial number are mandatory for a device.");
	public static ServiceError _305 = new ServiceError(305, "Error creating device", "Error creating device");
	public static ServiceError _306 = new ServiceError(306, "Error finding devices", "Error finding devices");
	public static ServiceError _307 = new ServiceError(307, "Serial number reuired", "Serial number required");
	public static ServiceError _308 = new ServiceError(308, "None or Too many devices found!!", "None or Too many devices found!!");
	public static ServiceError _309 = new ServiceError(309, "Error linking device", "error updating device details");
	public static ServiceError _310 = new ServiceError(310, "Device allready linked", "Device allready linked");
	
	
	public static ServiceError _400 = new ServiceError(400, "User verification failed", "User verification failed");
	public static ServiceError _401 = new ServiceError(401, "Email, password, status are mandaotry fields", "Email, password, status are mandaotry fields");
	public static ServiceError _402 = new ServiceError(402, "Error creating user", "Error creating user");
	public static ServiceError _403 = new ServiceError(403, "Min password length is 8", "Min password length is 8");
	
	
	public static ServiceError _502 = new ServiceError(502, "Error creating user preferences", "Error creating user preferences");
	public static ServiceError _503 = new ServiceError(503, "Error finding user preference", "Error finding user preference");
	public static ServiceError _504 = new ServiceError(504, "Error updating user preference", "Error updating user preference");

}
