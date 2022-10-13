package com.oracle.cloud.wearable.tcp.model.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "device")
@JsonInclude(JsonInclude.Include. NON_NULL)
@Data
public class Device {

    @Id
    private Long id;
    private String serialNumber;
    private String status;
    private Date activationDate;
    private String deviceType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}