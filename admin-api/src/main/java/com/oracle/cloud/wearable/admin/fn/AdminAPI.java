package com.oracle.cloud.wearable.admin.fn;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.RuntimeContext;
import com.fnproject.fn.api.httpgateway.HTTPGatewayContext;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.oracle.cloud.wearable.admin.Device;
import com.oracle.cloud.wearable.admin.DeviceList;
import com.oracle.cloud.wearable.admin.LoginRequest;
import com.oracle.cloud.wearable.admin.LoginResponse;
import com.oracle.cloud.wearable.admin.Request;
import com.oracle.cloud.wearable.admin.Response;
import com.oracle.cloud.wearable.admin.User;
import com.oracle.cloud.wearable.admin.UserPreferences;
import com.oracle.cloud.wearable.admin.Response.ResponseBuilder;
import com.oracle.cloud.wearable.admin.response.Error;
import com.oracle.cloud.wearable.admin.service.DeviceService;
import com.oracle.cloud.wearable.admin.service.SecurityService;
import com.oracle.cloud.wearable.admin.service.ServiceException;
import com.oracle.cloud.wearable.admin.service.ServiceFactory;
import com.oracle.cloud.wearable.admin.service.UserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class AdminAPI {

	private static final Logger log = Logger.getLogger(AdminAPI.class.getName());
	
	private static final String JAVA_LOGGING_ENV_PREFIX = "oci_java_logging_lib_";
	
	private static final String DATA_SOURCE_URL_OCID = "DATA_SOURCE_URL_OCID";
	private static final String DATA_SOURCE_USER_OCID = "DATA_SOURCE_USER_OCID";
	private static final String DATA_SOURCE_PASS_OCID = "DATA_SOURCE_PASS_OCID";
	private static final String HMAC_KEY_SECRET_OCID ="HMAC_KEY_SECRET_OCID";

	private static final String ENV_CONTEXT_PATH = "ENV_CONTEXT_PATH";

	private static final String ENDPOINT_LIST_DEVICE = "GET /getDevice";
	private static final String ENDPOINT_LIST_USER = "GET /getUser";
	private static final String ENDPOINT_LIST_USER_PREF = "GET /getUserPref";

	private static final String ENDPOINT_ADD_USER = "POST /addUser";
	private static final String ENDPOINT_ADD_DEVICE = "POST /addDevice";
	private static final String ENDPOINT_ADD_USER_PREF = "POST /addUserPref";
	
	private static final String ENDPOINT_USER_LOGIN = "POST /login";
	private static final String ENDPOINT_LINK_DEVICE = "POST /link";
	
	private SecurityService ss = SecurityService.getInstance();

	//private static final String ENDPOINT_UPDATE_USER_PREF = "POST /updateUserPref";
	//private static final String ENDPOINT_UPDATE_USER = "POST /updateUser";
	//private static final String ENDPOINT_UPDATE_DEVICE = "POST /updateDevice";

	//private static final String USER_ID = "userId";
	private static final String USERNAME = "username";
	private static final String SERIAL_NUMBER = "serialNo";
	
	private MysqlDataSource dataSource;

	private static ObjectMapper objectMapper; 
	
	private Key key;
	
	public AdminAPI() {
		
	}
	
	@FnConfiguration
	public void init(RuntimeContext ctx) {
		
		Optional<ObjectMapper> attribute = ctx.getAttribute("com.fnproject.fn.runtime.coercion.jackson.JacksonCoercion.om", ObjectMapper.class);
		
		attribute.ifPresent(om -> {om.writerWithDefaultPrettyPrinter();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		System.out.println("OM present");
		});
		
		if(!attribute.isPresent()) {
			System.out.println("OM not present");
			objectMapper = new ObjectMapper();
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			ctx.setAttribute("com.fnproject.fn.runtime.coercion.jackson.JacksonCoercion.om",objectMapper);
		}
		
		
		System.out.println("Initializing logger");
		final LogManager logManager = LogManager.getLogManager();

		StringBuilder builder = new StringBuilder();

		Map<String, String> environmentMap = ctx.getConfiguration();
		environmentMap.entrySet().stream().filter(entry -> entry.getKey().startsWith(JAVA_LOGGING_ENV_PREFIX))
				.forEach(entry -> {
					// replace all "_" with "." as "." not permitted in env config
					final String key = entry.getKey().replaceFirst(JAVA_LOGGING_ENV_PREFIX, "").replaceAll("_", ".");
					
					final String value = entry.getValue();
					builder.append(key).append("=").append(value).append(System.lineSeparator());
				});

		InputStream configStream = null;
		configStream = new ByteArrayInputStream(builder.toString().getBytes());

		try {
			logManager.readConfiguration(configStream);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		createDataSource(environmentMap);
		
		try {
			 //"tglKVRc9HKbGzOlUB7MbKwJmFxBlyVPLXJaC0+pLebE=";
			
			key = Keys.hmacShaKeyFor(ss.getSecretByOCID(environmentMap.get(HMAC_KEY_SECRET_OCID)));
		
		}catch (Exception e) {
			e.printStackTrace();
			key=null;
		}
		
	}

	private void createDataSource(Map<String, String> environmentMap) {
		
		log.log(Level.FINE, "Initializing Datasource");
		try {
			
		final String url = new String(ss.getSecretByOCID(environmentMap.get(DATA_SOURCE_URL_OCID)));
		final String user = new String(ss.getSecretByOCID(environmentMap.get(DATA_SOURCE_USER_OCID)));
		final String pass = new String(ss.getSecretByOCID(environmentMap.get(DATA_SOURCE_PASS_OCID)));

		if(Utils.isEmptyString(url) || Utils.isEmptyString(pass) || Utils.isEmptyString(user)) {
			log.log(Level.FINE, "required datasource values not provided");
			return;
		}
		
		dataSource = new MysqlDataSource();
		
		dataSource.setUrl(url);
		dataSource.setPassword(pass);
		dataSource.setUser(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public Response handleRequest(String postDataInput, HTTPGatewayContext ctx) {
		
		String responseCode = UUID.randomUUID().toString();
		
		log.log(Level.FINE, "tracking request with response code "+responseCode);
		
		String contextPath =  ctx.getInvocationContext().getRuntimeContext().getConfiguration().get(ENV_CONTEXT_PATH);
		
		if(contextPath==null)
			return Response.getBuilder().setResponseId(responseCode).setError(new Error(500,"Configuration Missing")).build();

		String path = ctx.getRequestURL().substring(contextPath.length());
		final String[] split = path.split("\\?");
		String matchingPath = ctx.getMethod()+" "+ (split.length>=1 ?split[0]:"");
		String queryString = (split.length>=2 ?split[1]:"");
				
		log.log(Level.FINE, "query Str-"+queryString);
		log.log(Level.FINE, "matching path-"+matchingPath);
		log.log(Level.FINE, "post data-"+postDataInput);
		
		String username = Utils.isEmptyString(queryString) ? null : Utils.getQueryParameter(queryString,USERNAME);

		try {
			switch (matchingPath) {
			
			case ENDPOINT_USER_LOGIN:
				LoginRequest req = Utils.parseJson(postDataInput, objectMapper, LoginRequest.class);
				return verifyLogin(req.getPass().getBytes(),req.getUsername()).build();
				
			case ENDPOINT_ADD_DEVICE:
				Device d = Utils.parseJson(postDataInput, objectMapper, Request.class).getDevice();
				return addDevice(username,d).setResponseId(responseCode).build();
			
			case ENDPOINT_LIST_DEVICE:
				String userId = Utils.getQueryParameter(queryString,USERNAME);
				return listDevice(userId).setResponseId(responseCode).build();
				
			case ENDPOINT_ADD_USER:	
				User user = Utils.parseJson(postDataInput, objectMapper, Request.class).getUser();
				return addUser(user).setResponseId(responseCode).build();
			case ENDPOINT_LIST_USER:
				return listUser(username).setResponseId(responseCode).build();
			case ENDPOINT_LIST_USER_PREF:
				return listUserPref(username).setResponseId(responseCode).build();
			case ENDPOINT_ADD_USER_PREF:
				UserPreferences userPreferences = Utils.parseJson(postDataInput, objectMapper, Request.class).getUserPreferences();
				return addUserPref(username,userPreferences).setResponseId(responseCode).build();
			
			case ENDPOINT_LINK_DEVICE:
				String serialNumber = Utils.getQueryParameter(queryString, SERIAL_NUMBER);
				return linkdevice(username,serialNumber).setResponseId(responseCode).build();

			default:
				return Response.getBuilder().setResponseId(responseCode).setError(new Error(502,"No matching path")).build();
			}
		} catch (UtilsException e) {
			e.printStackTrace();
			return Response.getBuilder().setResponseId(responseCode).setError(e.getErrorResponse()).build();
		}catch (Exception e) {
			e.printStackTrace();
			return Response.getBuilder().setResponseId(responseCode).setError(new Error(500,"Internal server Error")).build();
		}

	}

	private ResponseBuilder verifyLogin(byte[] bytes, String username2) {
		if(key==null)
			return Response.getBuilder().setError(new Error(500, "Internal error"));
		
		UserService userService;
		try {
			userService = ServiceFactory.getInstance().getService(UserService.class,dataSource);
		} catch (ServiceException e) {
			e.printStackTrace();
			return Response.getBuilder().setError(e.getError());
		}
		
		try {
			userService.verfiyCredentials(bytes, username2);
		} catch (ServiceException e) {
			e.printStackTrace();
			return Response.getBuilder().setError(e.getError());
		}
		
		
		
		
		long timeInMs = System.currentTimeMillis() + (20*60*1000);
		
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("scope", "USER");
		String toen = Jwts.builder().setSubject(username2)
				.setExpiration(new Date(timeInMs))
				.addClaims(claims)
				.signWith(key).compact();
		
		LoginResponse resp = new LoginResponse();
		resp.setToken(toen);
		
		return Response.getBuilder().setResponse(resp);
	}

	private ResponseBuilder linkdevice(String username2, String serialNumber) {
		
		if(Utils.isEmptyString(serialNumber))
			return Response.getBuilder().setError(new Error(300,"Serial Number is mandatory"));
		
		DeviceService ds;
		try {
			ds = ServiceFactory.getInstance().getService(DeviceService.class,dataSource);
		} catch (ServiceException e) {
			return Response.getBuilder().setError(e.getError());
		}
		
		try {
			Device d = new Device();
			d.setDeviceSerialNumber(serialNumber);
			User user = new User();
			user.setUsername(username2);
			ds.linkDevice(user,d);
			return Response.getBuilder().setResponse("Device Added");
		} catch (ServiceException e) {
			e.printStackTrace();
			return Response.getBuilder().setError(e.getError());
		}
	}

	private ResponseBuilder addUserPref(String username, UserPreferences userPreferences) {
		UserService userService;
		try {
			userService = ServiceFactory.getInstance().getService(UserService.class,dataSource);
		} catch (ServiceException e) {
			e.printStackTrace();
			return Response.getBuilder().setError(e.getError());
		}
		
		try {
			userService.addUserPreferences(username,userPreferences);
		} catch (ServiceException e) {
			e.printStackTrace();
			return Response.getBuilder().setError(e.getError());
		}
		return Response.getBuilder().setResponse("User preferences added.");
	}

	private ResponseBuilder listUserPref(String username2) {
		UserService userService;
		try {
			userService = ServiceFactory.getInstance().getService(UserService.class,dataSource);
		} catch (ServiceException e) {
			e.printStackTrace();
			return Response.getBuilder().setError(e.getError());
		}
		
		UserPreferences userPref;
		try {
			userPref = userService.getUserPreferences(username2);
		} catch (ServiceException e) {
			e.printStackTrace();
			return Response.getBuilder().setError(e.getError());
		}
		if(userPref==null)
			return Response.getBuilder().setResponse("no user preferences found");
		else
			return Response.getBuilder().setResponse(userPref);
	}

	private ResponseBuilder listUser(String username2) {
		UserService userService;
		try {
			userService = ServiceFactory.getInstance().getService(UserService.class,dataSource);
		} catch (ServiceException e) {
			e.printStackTrace();
			return Response.getBuilder().setError(e.getError());
		}
		
		User user;
		try {
			user = userService.findUserByUserName(username2);
		} catch (ServiceException e) {
			e.printStackTrace();
			return Response.getBuilder().setError(e.getError());
		}
		if(user==null)
			return Response.getBuilder().setResponse("No User Found");
		else
			return Response.getBuilder().setResponse(user);
	}

	private ResponseBuilder addUser(User user) {
		UserService userService;
		try {
			userService = ServiceFactory.getInstance().getService(UserService.class,dataSource);
		} catch (ServiceException e) {
			e.printStackTrace();
			return Response.getBuilder().setError(e.getError());
		}
		
		try {
			userService.addUser(user);
		} catch (ServiceException e) {
			e.printStackTrace();
			return Response.getBuilder().setError(e.getError());
		}
		
		return Response.getBuilder().setResponse("User Added");
	}


	private ResponseBuilder addDevice(String userName, Device device) {
		DeviceService ds;
		try {
			ds = ServiceFactory.getInstance().getService(DeviceService.class,dataSource);
		} catch (ServiceException e) {
			return Response.getBuilder().setError(e.getError());
		}
		
		try {
			ds.createDevice(userName,device);
			return Response.getBuilder().setResponse("Device Added");
		} catch (ServiceException e) {
			e.printStackTrace();
			return Response.getBuilder().setError(e.getError());
		}
		
	}


	private ResponseBuilder listDevice(String userId) {
		
		DeviceService ds;
		try {
			ds = ServiceFactory.getInstance().getService(DeviceService.class,dataSource);
		} catch (ServiceException e) {
			return Response.getBuilder().setError(e.getError());
		}
		
		Collection<Device> devices;
		try {
			devices = ds.getDevices(userId);
			return Response.getBuilder().setResponse(new DeviceList(devices));
		} catch (ServiceException e) {
			e.printStackTrace();
			return Response.getBuilder().setError(e.getError());
		}
		
	}


}
