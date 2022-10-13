package com.oracle.cloud.wearable.notification.util;

import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimplePrivateKeySupplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.function.Supplier;

@Slf4j
@Component
@Scope("singleton")
public final class AuthenticationProvider {

    private static final String ENV_KEY_AUTH_PROFILE = "OCI_AUTH_PROFILE";
    private static final String ENV_KEY_TENANT = "OCI_TENANT";
    private static final String ENV_KEY_USER_ID = "OCI_USER_ID";
    private static final String ENV_KEY_FINGER_PRINT = "OCI_USER_FINGER_PRINT";
    private static final String ENV_KEY_PVT_KEY_FILE_PATH = "OCI_PVT_KEY_PATH";

    private BasicAuthenticationDetailsProvider provider;

    public AuthenticationProvider() {
        final String ociAuthProfile = System.getenv().get(ENV_KEY_AUTH_PROFILE);
        this.provider = getProvider(ociAuthProfile);
    }

    public BasicAuthenticationDetailsProvider getProvider() {
        return this.provider;
    }

    private BasicAuthenticationDetailsProvider getProvider(final String ociAuthProfile) {
        if (ociAuthProfile == null
                || ociAuthProfile.isEmpty()
                || ociAuthProfile.trim().equalsIgnoreCase(AuthProfiles.INSTANCE_PRINCIPAL.getProfile())) {
            log.info("Using Instance Principal for authentication");

            return InstancePrincipalsAuthenticationDetailsProvider.builder().build();
        } else if (ociAuthProfile.trim().equalsIgnoreCase(AuthProfiles.CONFIG_FILE.getProfile())) {
            try {
                log.info("Using Private key supplier authentication provider for authentication");

                final Supplier<InputStream> privateKeySupplier =
                        new SimplePrivateKeySupplier(System.getenv().get(ENV_KEY_PVT_KEY_FILE_PATH));
                return SimpleAuthenticationDetailsProvider.builder()
                        .tenantId(System.getenv().get(ENV_KEY_TENANT))
                        .userId(System.getenv().get(ENV_KEY_USER_ID))
                        .fingerprint(System.getenv().get(ENV_KEY_FINGER_PRINT))
                        .privateKeySupplier((com.google.common.base.Supplier<InputStream>) privateKeySupplier)
                        .build();
            } catch (Exception e) {
                log.error("Error reading the config file for authentication", e);
                throw new RuntimeException(e);
            }
        } else {
            log.warn(
                    "Incorrect oci auth profile value {} found in env variable oci.auth.profile, queue consumer may not work",
                    ociAuthProfile);
            throw new RuntimeException(
                    "Incorrect OCI Auth Profile value " + ociAuthProfile + " found in env variable");
        }
    }
}
