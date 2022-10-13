package com.oracle.cloud.wearable.authorizer.fn;

import java.security.Key;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.RuntimeContext;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class Authorizer {

	private static final Logger log = Logger.getLogger(Authorizer.class.getName());
	
	private SecurityService ss = SecurityService.getInstance();

	private static ObjectMapper objectMapper; // = new ObjectMapper();
	
	private static final String HMAC_KEY_SECRET_OCID ="HMAC_KEY_SECRET_OCID";
	
	private static final DateTimeFormatter ISO8601 = DateTimeFormatter.ISO_DATE_TIME;
    private static final String TOKEN_BEARER_PREFIX = "Bearer ";
    
    private Key key;

    public static class Input {
        public String type;
        public Map<String,String> data;
    }

    public static class Result {
        // required
        public boolean active = false;
        public String principal;
        public String[] scope;
        public String expiresAt;

        // optional
        public String wwwAuthenticate;

        // optional
        public String clientId;

        // optional context
        public Map<String, Object> context;
    }
	
	public Authorizer() {
		
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
			//objectMapper.writerWithDefaultPrettyPrinter();
			ctx.setAttribute("com.fnproject.fn.runtime.coercion.jackson.JacksonCoercion.om",objectMapper);
		}
		
		try {
			Map<String, String> environmentMap = ctx.getConfiguration();
			key = Keys.hmacShaKeyFor(ss.getSecretByOCID(environmentMap.get(HMAC_KEY_SECRET_OCID)));
		
		}catch (Exception e) {
			e.printStackTrace();
			key=null;
		}
		
	}



    public Result handleRequest(Input input) {
        System.out.println("oci-apigw-authorizer-idcs-java START");
        
        Result result = new Result();
        
        if(key==null) {
        	result.active = false;
            result.wwwAuthenticate = "Bearer error=\"Internal error\"";
            System.out.println("oci-apigw-authorizer-idcs-java END (Token)");
            return result;
        }
        	

        if (input.data == null || !input.data.containsKey("authorization_token")) {
            result.active = false;
            result.wwwAuthenticate = "Bearer error=\"missing_token\"";
            System.out.println("oci-apigw-authorizer-idcs-java END (Token)");
            return result;
        }
        
        String rawToken = input.data.get("authorization_token");

        // remove "Bearer " prefix in the token string before processing
        String token = rawToken.substring(TOKEN_BEARER_PREFIX.length());

        try {
        	Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            // Now that we can trust the contents of the JWT we can build the APIGW auth result
            result.active = true;

            result.principal =  jws.getBody().getSubject(); 
            result.scope =  jws.getBody().get("scope", String.class).split(" ");  
            result.expiresAt =  ISO8601.format(jws.getBody().getExpiration().toInstant().atOffset(ZoneOffset.UTC)); 

        } catch (JwtException e) {
            e.printStackTrace();

            result.active = false;
            result.wwwAuthenticate = "Bearer error=\"invalid_token\", error_description=\"" + e.getMessage() + "\"";
        } 

        System.out.println("oci-apigw-authorizer-idcs-java END");

        return result;
    }

}
