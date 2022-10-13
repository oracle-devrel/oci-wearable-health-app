package com.oracle.cloud.wearable.notification.service;

import com.oracle.cloud.wearable.notification.model.db.User;

public interface UserService<T> {
    User getUserByUsername(final String username);
}