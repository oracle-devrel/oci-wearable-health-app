package com.oracle.cloud.wearable.tcp.model.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
@JsonInclude(JsonInclude.Include. NON_NULL)
@Data
public class User {

    @Id
    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private String status;
}