CREATE DATABASE health_app CHARACTER SET utf8 COLLATE utf8_bin;

CREATE TABLE health_app.device (
	id bigint NOT NULL AUTO_INCREMENT,
    serial_number varchar(100) NOT NULL,
    status varchar(20) NOT NULL,
    activation_date date,
    user_id bigint,
    device_type varchar(20),
    PRIMARY KEY (id)
);

CREATE TABLE health_app.user (
    id bigint NOT NULL AUTO_INCREMENT,
    username varchar(50) NOT NULL,
    password varchar(50) NOT NULL,
    first_name varchar(50),
    last_name varchar(50),
    mobile varchar(50),
    email varchar(50),
    status varchar(20) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE health_app.user_alert_notifications (
    id bigint NOT NULL AUTO_INCREMENT,
    user_id bigint,
    device_id bigint,
    alert_id varchar(100),
    alert_parameter varchar(100),
    observed_value double,
    notification_time timestamp,
    notification_channel varchar(100),
    PRIMARY KEY (id)
);

CREATE TABLE health_app.user_preference (
    id bigint NOT NULL AUTO_INCREMENT,
    user_id bigint,
    preference varchar(1024),
    PRIMARY KEY (id)
);

ALTER TABLE health_app.device
ADD FOREIGN KEY (user_id) REFERENCES health_app.user(id);

ALTER TABLE health_app.device
ADD UNIQUE (serial_number);

ALTER TABLE health_app.user
ADD UNIQUE (username);

ALTER TABLE health_app.user_alert_notifications
ADD FOREIGN KEY (user_id) REFERENCES health_app.user(id);

ALTER TABLE health_app.user_alert_notifications
ADD FOREIGN KEY (device_id) REFERENCES health_app.device(id);

ALTER TABLE health_app.user_preference
ADD FOREIGN KEY (user_id) REFERENCES health_app.user(id);