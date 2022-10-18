package com.oracle.cloud.wearable.streaming.analytics;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Alert {
	private String deviceSerialNumber;  
	private String username;
	@Builder.Default
	private String alertID=UUID.randomUUID().toString();  //UUID
	private Date alertDateTime=new Date(); //current data time
	@Builder.Default
	private String notificationChannel="EMAIL";//EMAIL
	private Integer threshold; //
	private Long observedValue; //average value
 	private String alertingParameter; //field name
 	@Builder.Default
 	private Integer notificationFrequency=5; //5 minutes

	
}