package com.oracle.cloud.wearable.notification.service.impl;

import com.oracle.cloud.wearable.notification.db.UserRepository;
import com.oracle.cloud.wearable.notification.model.db.User;
import com.oracle.cloud.wearable.notification.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService<User> {

  @Autowired
  private UserRepository userRepository;

  public User getUserByUsername(final String username) {
    final Optional<User> user = userRepository.getUserByUsername(username);
    if(!user.isPresent()) {
      return null;
    }
    return user.get();
  }
}
