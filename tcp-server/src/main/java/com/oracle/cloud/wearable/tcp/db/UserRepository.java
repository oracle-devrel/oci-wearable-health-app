package com.oracle.cloud.wearable.tcp.db;

import com.oracle.cloud.wearable.tcp.model.db.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //
}