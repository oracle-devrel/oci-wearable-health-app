package com.oracle.cloud.wearable.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class UserPreferences {
	
	@JsonIgnore
	private Integer id;
	
	@JsonIgnore
	private Integer userId;

	@JsonProperty
	private String alertThresholdForHearRate;

	@JsonProperty
	private String alertThresholdForBPHigh;

	@JsonProperty
	private String alertThresholdForBPLow;

	@JsonProperty
	private String alertThresholdForSPO2;

	@JsonProperty
	private String alertThresholdForTemp;

	@JsonProperty
	private String preferedAlertChannel;

	@JsonProperty
	private String emergencyEmail;
	
	@JsonProperty
	private Integer notificationFrequency;

	@JsonProperty
	private String emergencyMobile;

	public String getAlertThresholdForHearRate() {
		return alertThresholdForHearRate;
	}

	public void setAlertThresholdForHearRate(String alertThresholdForHearRate) {
		this.alertThresholdForHearRate = alertThresholdForHearRate;
	}

	public String getAlertThresholdForBPHigh() {
		return alertThresholdForBPHigh;
	}

	public void setAlertThresholdForBPHigh(String alertThresholdForBPHigh) {
		this.alertThresholdForBPHigh = alertThresholdForBPHigh;
	}

	public String getAlertThresholdForBPLow() {
		return alertThresholdForBPLow;
	}

	public void setAlertThresholdForBPLow(String alertThresholdForBPLow) {
		this.alertThresholdForBPLow = alertThresholdForBPLow;
	}

	public String getAlertThresholdForSPO2() {
		return alertThresholdForSPO2;
	}

	public void setAlertThresholdForSPO2(String alertThresholdForSPO2) {
		this.alertThresholdForSPO2 = alertThresholdForSPO2;
	}

	public String getAlertThresholdForTemp() {
		return alertThresholdForTemp;
	}

	public void setAlertThresholdForTemp(String alertThresholdForTemp) {
		this.alertThresholdForTemp = alertThresholdForTemp;
	}

	public String getPreferedAlertChannel() {
		return preferedAlertChannel;
	}

	public void setPreferedAlertChannel(String preferedAlertChannel) {
		this.preferedAlertChannel = preferedAlertChannel;
	}

	public String getEmergencyEmail() {
		return emergencyEmail;
	}

	public void setEmergencyEmail(String emergencyEmail) {
		this.emergencyEmail = emergencyEmail;
	}

	public String getEmergencyMobile() {
		return emergencyMobile;
	}

	public void setEmergencyMobile(String emergencyMobile) {
		this.emergencyMobile = emergencyMobile;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getNotificationFrequency() {
		return notificationFrequency;
	}
	
	public void setNotificationFrequency(Integer notificationFrequency) {
		this.notificationFrequency = notificationFrequency;
	}
}
