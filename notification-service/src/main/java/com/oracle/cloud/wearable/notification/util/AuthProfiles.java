package com.oracle.cloud.wearable.notification.util;

/**
 *
 */
public enum AuthProfiles {
    CONFIG_FILE("configFile"),
    INSTANCE_PRINCIPAL("InstancePrincipal");

    private String profile;

    private AuthProfiles(final String profile) {
        this.profile = profile;
    }

    public String getProfile() {
        return profile;
    }
}