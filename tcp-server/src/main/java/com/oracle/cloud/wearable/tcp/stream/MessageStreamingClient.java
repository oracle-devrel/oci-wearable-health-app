package com.oracle.cloud.wearable.tcp.stream;

import com.oracle.bmc.auth.*;
import com.oracle.bmc.streaming.StreamClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.function.Supplier;

import static com.oracle.cloud.wearable.tcp.util.AuthProfiles.CONFIG_FILE;
import static com.oracle.cloud.wearable.tcp.util.AuthProfiles.INSTANCE_PRINCIPAL;

/** */
@Component
@Scope("singleton")
@Slf4j
public class MessageStreamingClient {

  private static final String ENV_KEY_AUTH_PROFILE = "OCI_AUTH_PROFILE";
  private static final String ENV_KEY_MESSAGING_ENDPOINT = "OCI_STREAM_MESSAGING_ENDPOINT";
  private static final String ENV_KEY_TENANT = "OCI_TENANT";
  private static final String ENV_KEY_USER_ID = "OCI_USER_ID";
  private static final String ENV_KEY_FINGER_PRINT = "OCI_USER_FINGER_PRINT";
  private static final String ENV_KEY_PVT_KEY_FILE_PATH = "OCI_PVT_KEY_PATH";
  private volatile StreamClient streamClient;

  MessageStreamingClient() {
    initiateStreamClient();
  }

  StreamClient getStreamClient() {
    if (streamClient == null) {
      initiateStreamClient();
    }
    return streamClient;
  }

  private void initiateStreamClient() {
    if (streamClient == null) {
      synchronized (this) {
        if (streamClient == null) {
          final String messagingEndpoint = System.getenv().get(ENV_KEY_MESSAGING_ENDPOINT);
          final String ociAuthProfile = System.getenv().get(ENV_KEY_AUTH_PROFILE);
          log.info(
              "Using the messaging endpoint as {} and auth profile as {} for creating stream client",
              messagingEndpoint,
              ociAuthProfile);

          streamClient =
              StreamClient.builder()
                  .endpoint(messagingEndpoint)
                  .build(getAuthProvider(ociAuthProfile));
        }
      }
    }
  }

  private BasicAuthenticationDetailsProvider getAuthProvider(final String ociAuthProfile) {
    if (ociAuthProfile == null
        || ociAuthProfile.isEmpty()
        || ociAuthProfile.trim().equalsIgnoreCase(INSTANCE_PRINCIPAL.getProfile())) {
      log.info("Using Instance Principal for authentication");

      return InstancePrincipalsAuthenticationDetailsProvider.builder().build();
    } else if (ociAuthProfile.trim().equalsIgnoreCase(CONFIG_FILE.getProfile())) {
      try {
        log.info("Using Private key supplier authentication provider for authentication");

        final Supplier<InputStream> privateKeySupplier = new SimplePrivateKeySupplier(System.getenv().get(ENV_KEY_PVT_KEY_FILE_PATH));
        return SimpleAuthenticationDetailsProvider.builder().tenantId(System.getenv().get(ENV_KEY_TENANT))
                .userId(System.getenv().get(ENV_KEY_USER_ID)).fingerprint(System.getenv().get(ENV_KEY_FINGER_PRINT))
                .privateKeySupplier((com.google.common.base.Supplier<InputStream>) privateKeySupplier).build();
      } catch (Exception e) {
        log.error("Error reading the config file for authentication", e);
        throw new RuntimeException(e);
      }
    } else {
      log.warn(
          "Incorrect oci auth profile value {} found in env variable oci.auth.profile, message publishing may not work",
          ociAuthProfile);
      throw new RuntimeException(
          "Incorrect OCI Auth Profile value " + ociAuthProfile + " found in env variable");
    }
  }
}