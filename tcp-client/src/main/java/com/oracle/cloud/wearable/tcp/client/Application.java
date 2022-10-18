package com.oracle.cloud.wearable.tcp.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.oracle.cloud.wearable.tcp.client.util.PlaceHolder;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws FileNotFoundException {
		if (args == null || args.length != 1) {
			System.out.println("Incorrect usage, pass the path of the file containing the device serial numbers");
			return;
		}

		final String filePath = args[0];
		final Scanner scanner = new Scanner(new File(filePath));

		while (scanner.hasNext()) {
			final String line = scanner.next();
			final String[] data = line.split(",");
			PlaceHolder.DEVICE_SERIAL_NUMBERS.put(data[0], Boolean.valueOf(data[1]));
		}

		scanner.close();

		System.out
				.println("Starting client for devices  " + PlaceHolder.DEVICE_SERIAL_NUMBERS.size());
		SpringApplication.run(Application.class, args);
	}
}