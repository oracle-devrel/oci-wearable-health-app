package com.oracle.cloud.wearable.notification.util;

import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;
import com.oracle.bmc.secrets.requests.GetSecretBundleRequest;
import com.oracle.bmc.secrets.responses.GetSecretBundleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecretUtil {

  private static final String ENV_KEY_QUEUE_REGION_ID = "OCI_QUEUE_REGION_ID";

  private AuthenticationProvider authenticationProvider;

  private volatile SecretsClient secretsClient;

  @Autowired
  public SecretUtil(final AuthenticationProvider provider) {
    this.authenticationProvider = provider;
    createSecretClient();
  }

  public String getSecretValue(final String secretOCID) {
    final GetSecretBundleRequest getSecretBundleRequest =
        GetSecretBundleRequest.builder()
            .secretId(secretOCID)
            .stage(GetSecretBundleRequest.Stage.Current)
            .build();
    final GetSecretBundleResponse getSecretBundleResponse =
        secretsClient.getSecretBundle(getSecretBundleRequest);
    final Base64SecretBundleContentDetails base64SecretBundleContentDetails =
        (Base64SecretBundleContentDetails)
            getSecretBundleResponse.getSecretBundle().getSecretBundleContent();
    final byte[] secretValueDecoded =
        Base64.decodeBase64(base64SecretBundleContentDetails.getContent());
    return new String(secretValueDecoded);
  }

  private void createSecretClient() {
    if (secretsClient == null) {
      synchronized (this) {
        if (secretsClient == null) {
          secretsClient = new SecretsClient(authenticationProvider.getProvider());
          secretsClient.setRegion(System.getenv().get(ENV_KEY_QUEUE_REGION_ID));
        }
      }
    }
  }
}