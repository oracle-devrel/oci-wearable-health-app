package com.oracle.cloud.wearable.authorizer.fn;

import org.apache.commons.codec.binary.Base64;

import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;
import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;
import com.oracle.bmc.secrets.requests.GetSecretBundleRequest;
import com.oracle.bmc.secrets.responses.GetSecretBundleResponse;

public class SecurityService {
	
	private final ResourcePrincipalAuthenticationDetailsProvider provider = ResourcePrincipalAuthenticationDetailsProvider.builder().build();
	
	private SecretsClient sc;
	
	private static SecurityService instance = new SecurityService();
	
	private SecurityService() {
		try {

			sc = SecretsClient.builder().build(provider);
		} catch (Throwable ex) {
			System.err.println("Failed to instantiate secrets client - " + ex.getMessage());
		}
	}
	
	public byte[] getSecretByOCID(String ocid) {
		
		try {
			GetSecretBundleRequest getSecretBundleRequest = GetSecretBundleRequest.builder().secretId(ocid)
					.stage(GetSecretBundleRequest.Stage.Current).build();
			
			GetSecretBundleResponse getSecretBundleResponse = sc.getSecretBundle(getSecretBundleRequest);
			
			Base64SecretBundleContentDetails base64SecretBundleContentDetails = (Base64SecretBundleContentDetails) getSecretBundleResponse
					.getSecretBundle().getSecretBundleContent();
			
			return Base64.decodeBase64(base64SecretBundleContentDetails.getContent());
		} catch (Exception e) {
			throw new RuntimeException("Unable to get secret", null);
		}
		
	}

	public static SecurityService getInstance() {
		return instance;
	}

}
