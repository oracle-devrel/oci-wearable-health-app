package com.oracle.cloud.wearable.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.oracle.cloud.wearable.admin.service.ServiceError;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class Response {


	@JsonProperty
	private String responseId;

	@JsonProperty("response")
	private Object response;
	
	private Response() {
	}

	private Response( Object r) {
		response = r;
	}

	public void setResponse(Object response) {
		this.response = response;
	}

	public Object getResponse() {
		return response;
	}
	
	public String getResponseId() {
		return responseId;
	}
	
	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}
	
	public static ResponseBuilder getBuilder() {
		return new ResponseBuilder();
	}
	
	public static class ResponseBuilder {
		
		private Response r;
		
		public ResponseBuilder() {
			this.r = new Response();
		}
		
		public ResponseBuilder setError(com.oracle.cloud.wearable.admin.response.Error error) {
			r.setResponse(error);
			return this;
		}
		
		public ResponseBuilder setError(ServiceError error) {
			r.setResponse(new com.oracle.cloud.wearable.admin.response.Error(error.getCode(),error.getUserMessage()));
			return this;
		}
		
		public ResponseBuilder setResponse(Object obj) {
			r.setResponse(obj);
			return this;
		}
		
		public ResponseBuilder setResponseId(String code) {
			r.setResponseId(code);
			return this;
		}
		
		public Response build() {
			return r;
		}

	}

}
