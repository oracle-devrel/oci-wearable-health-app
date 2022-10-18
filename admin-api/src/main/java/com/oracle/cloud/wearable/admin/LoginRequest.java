package com.oracle.cloud.wearable.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class LoginRequest {

	@JsonProperty
	private String username;
	
	@JsonProperty
	private String pass;
	
	public String getPass() {
		return pass;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
}
